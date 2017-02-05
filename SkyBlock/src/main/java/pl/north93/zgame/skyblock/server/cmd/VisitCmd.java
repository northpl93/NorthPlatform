package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class VisitCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private     INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private     SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private     ResourceBundle  messages;

    public VisitCmd()
    {
        super("visit", "odwiedz");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.visit.args");
            return;
        }

        final String visited = args.asString(0);
        final UUID islandId = this.islandIdFromOwnerName(visited);
        if (islandId == null)
        {
            sender.sendMessage(this.messages, "cmd.visit.no_player");
            return;
        }
        this.server.getSkyBlockManager().visitIsland(islandId, sender.getName());
    }

    private UUID islandIdFromOwnerName(final String owner)
    {
        final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getOnlinePlayer(owner);
        if (onlinePlayer.isAvailable())
        {
            return SkyPlayer.get(onlinePlayer).getIslandId();
        }

        final IOfflinePlayer offlinePlayer = this.networkManager.getOfflinePlayer(owner);
        if (offlinePlayer != null)
        {
            return SkyPlayer.get(offlinePlayer).getIslandId();
        }

        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
