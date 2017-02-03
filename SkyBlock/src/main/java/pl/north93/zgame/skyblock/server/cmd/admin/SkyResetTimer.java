package pl.north93.zgame.skyblock.server.cmd.admin;

import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class SkyResetTimer extends NorthCommand
{
    private ApiCore             apiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private     INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private     SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private     ResourceBundle  messages;

    public SkyResetTimer()
    {
        super("skyresettimer");
        this.setPermission("skyblock.admin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final String player = args.length() == 1 ? args.asString(0) : sender.getName();

        final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getOnlinePlayer(player);

        if (! onlinePlayer.isAvailable())
        {
            sender.sendMessage("&cGracz " + player + " musi byc online!");
            return;
        }
        else
        {
            sender.sendMessage("&cResetowanie timera dla " + player);
        }

        final SkyPlayer skyPlayer = SkyPlayer.get(onlinePlayer);
        skyPlayer.setIslandCooldown(0);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
