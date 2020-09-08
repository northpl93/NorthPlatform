package pl.north93.northplatform.lobby.chest.cmd;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import pl.north93.northplatform.api.bukkit.player.IBukkitPlayers;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.lobby.chest.ChestService;
import pl.north93.northplatform.lobby.chest.ChestType;
import pl.north93.northplatform.lobby.chest.loot.ChestLootService;
import pl.north93.northplatform.lobby.chest.loot.ILoot;
import pl.north93.northplatform.lobby.chest.loot.ItemShardLoot;
import pl.north93.northplatform.lobby.chest.loot.LootResult;
import pl.north93.northplatform.lobby.chest.opening.ChestOpeningController;
import pl.north93.northplatform.lobby.chest.opening.IOpeningSession;

public class TestLoot extends NorthCommand
{
    @Inject
    private ChestService chestService;
    @Inject
    private IBukkitPlayers bukkitPlayers;
    @Inject
    private ChestLootService chestLootService;
    @Inject
    private ChestOpeningController chestOpeningController;

    public TestLoot()
    {
        super("testloot");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final INorthPlayer player = this.bukkitPlayers.getPlayer(sender);

        final IOpeningSession openingSession = this.chestOpeningController.getSession(player);
        final ChestType type = this.chestService.getType(openingSession.getConfig().getChestType());

        new Thread( () ->
        {
            final int[] rarities = {0, 0, 0, 0};
            for (int i = 0; i < 100; i++)
            {
                final LootResult lootResult;
                try
                {
                    lootResult = this.chestLootService.generateLoot(type).get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                    return;
                }

                for (final ILoot iLoot : lootResult.getLoot())
                {
                    final Item item = ((ItemShardLoot) iLoot).getItem();
                    final int rarity = item.getRarity().ordinal();
                    rarities[rarity] = rarities[rarity] + 1;
                }
            }

            sender.sendMessage(Arrays.toString(rarities));
        }).start();
    }
}
