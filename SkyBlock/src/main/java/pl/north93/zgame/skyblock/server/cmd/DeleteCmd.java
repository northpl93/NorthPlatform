package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
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
        final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getOnlinePlayer(sender.getName());
        final SkyPlayer skyPlayer = SkyPlayer.get(onlinePlayer);
        if (! skyPlayer.hasIsland())
        {
            sender.sendMessage(this.messages, "error.you_must_have_island");
            return;
        }
        if (skyPlayer.getIslandRole().equals(IslandRole.MEMBER))
        {
            sender.sendMessage(this.messages, "error.you_must_be_owner");
            return;
        }

        if (args.length() == 1 && args.asText(0).equals("potwierdz"))
        {
            this.server.getSkyBlockManager().deleteIsland(skyPlayer.getIslandId());
            sender.sendMessage(this.messages, "info.deleted_island");
        }
        else
        {
            sender.sendMessage(this.messages, "info.confirm_island_deletion", label);
        }
    }
}
