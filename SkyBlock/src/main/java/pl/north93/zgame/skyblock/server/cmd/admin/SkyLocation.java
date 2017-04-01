package pl.north93.zgame.skyblock.server.cmd.admin;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.shared.api.utils.Coords2D;
import pl.north93.zgame.skyblock.shared.api.utils.Coords3D;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.server.world.IslandLocation;
import pl.north93.zgame.skyblock.server.world.WorldManager;

public class SkyLocation extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;

    public SkyLocation()
    {
        super("skylocation");
        this.setPermission("skyblock.admin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final IslandHostManager serverManager = this.server.getServerManager();
        final Location loc = player.getLocation();
        sender.sendMessage("&eworld: " + loc.getWorld().getName() + " x: " + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ());

        final WorldManager worldManager = serverManager.getWorldManager(player.getWorld());
        if (worldManager == null)
        {
            sender.sendMessage("&cBrak managera swiatow...");
            return;
        }

        sender.sendMessage("&eNazwa typu wysp: " + worldManager.getIslandConfig().getName());
        final Island byChunk = worldManager.getIslands().getByChunk(loc.getChunk());
        if (byChunk == null)
        {
            sender.sendMessage("&cBrak wyspy na tym chunku");
            return;
        }

        final IslandLocation isLoc = byChunk.getLocation();

        final Coords2D islandCoordinates = byChunk.getIslandCoordinates();
        sender.sendMessage("&eislandCoords: X:" + islandCoordinates.getX() + " Z:" + islandCoordinates.getZ());
        sender.sendMessage("&ecenterChunk: X:" + isLoc.getCenterChunkX() + " Z:" + isLoc.getCenterChunkZ());

        final Coords3D rel = isLoc.toRelative(loc);
        sender.sendMessage("&erelativeLoc: x:" + rel.getX() + " y:" + rel.getY() + " z:" + rel.getZ());
    }
}
