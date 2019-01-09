package pl.north93.zgame.skyblock.server.cmd.admin;

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

public class SkyAddMember extends NorthCommand
{
    @Inject
    private ApiCore         apiCore;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;

    public SkyAddMember()
    {
        super("skyaddmember");
        this.setPermission("skyblock.admin");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 2)
        {
            sender.sendRawMessage("&c/skyaddmember czyja_wyspa kogo_dodac");
            return;
        }

        final String nick1 = args.asString(0);
        final String nick2 = args.asString(1);
        if (nick1.equalsIgnoreCase(nick2))
        {
            sender.sendRawMessage("&cPodales dwa takie same nicki :<");
            return;
        }

        final IPlayersManager players = this.networkManager.getPlayers();
        try (final IPlayerTransaction member = players.transaction(nick1); final IPlayerTransaction newMember = players.transaction(nick2))
        {
            final SkyPlayer skyMember = SkyPlayer.get(member.getPlayer());
            final SkyPlayer skyNewMember = SkyPlayer.get(newMember.getPlayer());

            if (skyNewMember.hasIsland())
            {
                if (skyNewMember.getIslandRole().equals(IslandRole.OWNER))
                {
                    sender.sendRawMessage("&c" + newMember.getPlayer().getLatestNick() + " jest wlascicielem innej wyspy!");
                    return;
                }

                this.server.getIslandDao().modifyIsland(skyNewMember.getIslandId(), islandData ->
                {
                    islandData.removeMember(newMember.getPlayer().getUuid());
                });
                sender.sendRawMessage("&aUwaga: Gracza " + nick2 + " usunieto z jego poprzedniej wyspy.");
            }

            this.server.getIslandDao().modifyIsland(skyMember.getIslandId(), islandData ->
            {
                islandData.addMember(newMember.getPlayer().getUuid());
            });

            skyNewMember.setIsland(skyMember.getIslandId(), IslandRole.MEMBER);

            sender.sendRawMessage("&aPomyslnie dodano gracza do wyspy.");
        }
        catch (PlayerNotFoundException e)
        {
            sender.sendRawMessage("&cNie znaleziono gracza o nicku " + e.getPlayerName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
