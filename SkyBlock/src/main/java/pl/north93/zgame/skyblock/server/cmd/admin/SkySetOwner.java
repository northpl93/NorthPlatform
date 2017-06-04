package pl.north93.zgame.skyblock.server.cmd.admin;

import java.util.UUID;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class SkySetOwner extends NorthCommand
{
    @Inject
    private ApiCore         apiCore;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;

    public SkySetOwner()
    {
        super("skysetowner");
        this.setPermission("skyblock.admin");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 2)
        {
            sender.sendMessage("&c/skysetowner czyja_wyspa dla_kogo");
            return;
        }

        final String nick1 = args.asString(0);
        final String nick2 = args.asString(1);
        if (nick1.equalsIgnoreCase(nick2))
        {
            sender.sendMessage("&cPodales dwa takie same nicki :<");
            return;
        }

        final IPlayersManager players = this.networkManager.getPlayers();
        try (final IPlayerTransaction oldOwner = players.transaction(nick1); final IPlayerTransaction newOwner = players.transaction(nick2))
        {
            final SkyPlayer skyOldOwner = SkyPlayer.get(oldOwner.getPlayer());
            final SkyPlayer skyNewOwner = SkyPlayer.get(newOwner.getPlayer());

            if (skyNewOwner.hasIsland())
            {
                if (skyNewOwner.getIslandRole().equals(IslandRole.OWNER))
                {
                    sender.sendMessage("&c" + newOwner.getPlayer().getLatestNick() + " jest wlascicielem innej wyspy!");
                    return;
                }

                this.server.getIslandDao().modifyIsland(skyNewOwner.getIslandId(), islandData ->
                {
                    islandData.removeMember(newOwner.getPlayer().getUuid());
                });
                sender.sendMessage("&aUwaga: Gracza " + nick2 + " usunieto z jego poprzedniej wyspy.");
            }

            final UUID targetIsland = skyOldOwner.getIslandId();

            skyNewOwner.setIsland(targetIsland, IslandRole.OWNER);
            skyOldOwner.setIsland(targetIsland, IslandRole.MEMBER);

            this.server.getIslandDao().modifyIsland(targetIsland, islandData ->
            {
                islandData.setOwnerId(newOwner.getPlayer().getUuid());
                islandData.addMember(oldOwner.getPlayer().getUuid());
            });

            sender.sendMessage("&aPomyslnie zmieniono wlasciciela wyspy.");
        }
        catch (final PlayerNotFoundException e)
        {
            sender.sendMessage("&cNie znaleziono gracza o nicku " + e.getPlayerName());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}
