package pl.north93.zgame.skyblock.server.cmd;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class InvitesCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectMessages("SkyBlock")
    private MessagesBox     messages;

    public InvitesCmd()
    {
        super("invites", "zaproszenia");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(sender.getName()))
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());
            if (! skyPlayer.hasIsland())
            {
                sender.sendMessage(this.messages, "error.you_must_have_island");
                return;
            }

            if (args.length() == 0)
            {
                this.listInvites(sender, skyPlayer);
                if (skyPlayer.getIslandRole().equals(IslandRole.OWNER))
                {
                    sender.sendMessage(this.messages, "cmd.invites.help", label);
                }
            }
            else if (args.length() == 1)
            {
                final String args1 = args.asString(0);
                if (args1.equals("remove") || args1.equals("cofnij") || args1.equals("wyrzuc"))
                {
                    if (skyPlayer.getIslandRole().equals(IslandRole.MEMBER))
                    {
                        sender.sendMessage(this.messages, "error.you_must_be_owner");
                        return;
                    }
                    sender.sendMessage(this.messages, "cmd.invites.help", label);
                }
            }
            else if (args.length() == 2)
            {
                final String args1 = args.asString(0);
                if (args1.equals("remove") || args1.equals("cofnij") || args1.equals("wyrzuc"))
                {
                    if (skyPlayer.getIslandRole().equals(IslandRole.MEMBER))
                    {
                        sender.sendMessage(this.messages, "error.you_must_be_owner");
                        return;
                    }
                    this.server.getSkyBlockManager().leaveIsland(skyPlayer.getIslandId(), sender.getName(), args.asString(1), false);
                }
                else
                {
                    sender.sendMessage(this.messages, "cmd.invites.help", label);
                }
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    private void listInvites(final NorthCommandSender sender, final SkyPlayer skyPlayer)
    {
        this.server.getIslandDao().modifyIsland(skyPlayer.getIslandId(), islandData ->
        {
            final String members = islandData.getMembersUuid().stream()
                                             .map(this.networkManager::getNickFromUuid)
                                             .collect(Collectors.joining(", "));

            final String invites = islandData.getInvitations().stream()
                                             .map(this.networkManager::getNickFromUuid)
                                             .collect(Collectors.joining(", "));

            final String empty = this.messages.getMessage(sender.getLocale(), "cmd.invites.empty_list");
            sender.sendMessage(this.messages, "cmd.invites.members", StringUtils.isEmpty(members) ? empty : members);
            sender.sendMessage(this.messages, "cmd.invites.list", StringUtils.isEmpty(invites) ? empty : invites);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
