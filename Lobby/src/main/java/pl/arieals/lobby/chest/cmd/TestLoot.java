package pl.arieals.lobby.chest.cmd;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.bukkit.entity.Player;

import pl.arieals.globalshops.shared.Item;
import pl.arieals.lobby.chest.ChestService;
import pl.arieals.lobby.chest.ChestType;
import pl.arieals.lobby.chest.loot.ChestLootService;
import pl.arieals.lobby.chest.loot.ILoot;
import pl.arieals.lobby.chest.loot.ItemShardLoot;
import pl.arieals.lobby.chest.loot.LootResult;
import pl.arieals.lobby.chest.opening.ChestOpeningController;
import pl.arieals.lobby.chest.opening.IOpeningSession;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class TestLoot extends NorthCommand
{
    @Inject
    private ChestLootService chestLootService;
    @Inject
    private ChestService chestService;
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
        final Player player = (Player) sender.unwrapped();

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
