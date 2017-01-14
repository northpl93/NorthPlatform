package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandRole;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class LeaveCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private ResourceBundle  messages;

    public LeaveCmd()
    {
        super("leave", "opusc");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Value<IOnlinePlayer> onlineSender = this.networkManager.getOnlinePlayer(sender.getName());
        final SkyPlayer skyPlayer = SkyPlayer.get(onlineSender);

        if (! skyPlayer.hasIsland())
        {
            sender.sendMessage(this.messages, "error.you_must_have_island");
            return;
        }

        if (skyPlayer.getIslandRole().equals(IslandRole.OWNER))
        {
            sender.sendMessage(this.messages, "error.you_must_be_member");
            return;
        }

        this.server.getSkyBlockManager().leaveIsland(skyPlayer.getIslandId(), sender.getName(), sender.getName(), true);
    }
}
