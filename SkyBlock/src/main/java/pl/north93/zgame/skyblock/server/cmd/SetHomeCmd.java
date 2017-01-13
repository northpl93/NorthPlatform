package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.api.ServerMode;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;

public class SetHomeCmd extends NorthCommand
{
    private ApiCore         apiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private ResourceBundle  messages;

    public SetHomeCmd()
    {
        super("sethome", "ustawdom", "zmiendom");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        { // async because we're doing database update, getIsland query
            final SkyPlayer skyPlayer = SkyPlayer.get(this.networkManager.getOnlinePlayer(sender.getName()));
            if (! skyPlayer.hasIsland())
            {
                sender.sendMessage(this.messages, "error.you_must_have_island");
                return;
            }

            if (this.server.getServerMode().equals(ServerMode.LOBBY))
            {
                sender.sendMessage(this.messages, "error.you_must_be_inside_island");
                return;
            }

            final Island island = this.server.<IslandHostManager>getServerManager().getIsland(skyPlayer.getIslandId());
            if (island == null)
            {
                sender.sendMessage(this.messages, "error.you_must_be_inside_island");
                return;
            }

            final Player player = (Player) sender.unwrapped();
            final Location newLocation = player.getLocation();

            if (! island.getLocation().isInside(newLocation))
            {
                sender.sendMessage(this.messages, "error.you_must_be_inside_island");
                return;
            }

            island.setHomeLocation(newLocation);
            sender.sendMessage(this.messages, "info.home_changed");
        });
    }
}
