package pl.north93.zgame.skyblock.server.cmd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class DeleteCmd extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;
    @Inject @Messages("SkyBlock")
    private MessagesBox     messages;

    public DeleteCmd()
    {
        super("delete", "remove", "usun");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(sender.getName())) {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());

            if (!skyPlayer.hasIsland()) {
                sender.sendMessage(this.messages, "error.you_must_have_island");
                return;
            }
            if (skyPlayer.getIslandRole().equals(IslandRole.MEMBER)) {
                sender.sendMessage(this.messages, "error.you_must_be_owner");
                return;
            }

            if (args.length() == 1 && args.asText(0).equals("potwierdz")) {
                this.server.getSkyBlockManager().deleteIsland(skyPlayer.getIslandId());
                sender.sendMessage(this.messages, "info.deleted_island");
            } else if (args.length() == 0) {
                sender.sendMessage(this.messages, "info.confirm_island_deletion", label);
                if (!this.server.getServerManager().canGenerateIsland(skyPlayer)) {
                    sender.sendMessage(this.messages, "info.delete_has_cooldown");
                }
            } else {
                sender.sendMessage(this.messages, "cmd.delete.args");
            }
        }
        catch(final Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
