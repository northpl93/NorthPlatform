package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandRole;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class DeleteCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private ResourceBundle  messages;

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
