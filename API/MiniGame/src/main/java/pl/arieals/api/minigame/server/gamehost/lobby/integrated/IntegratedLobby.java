package pl.arieals.api.minigame.server.gamehost.lobby.integrated;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.lobby.ILobbyManager;
import pl.arieals.api.minigame.shared.api.GameMapConfig;

public class IntegratedLobby implements ILobbyManager
{
    @Override
    public void addPlayer(final LocalArena arena, final Player player)
    {
        final GameMapConfig activeMap = arena.getWorld().getCurrentMapConfig();
        final Location location = this.readSpawnLocation(activeMap.getProperties(), arena.getWorld().getCurrentWorld());

        player.teleport(location);
    }

    @Override
    public void removePlayer(final Player player)
    {
    }

    private Location readSpawnLocation(final Map<String, String> properties, final World world)
    {
        final double x = Double.parseDouble(properties.get("lobby-x"));
        final double y = Double.parseDouble(properties.get("lobby-y"));
        final double z = Double.parseDouble(properties.get("lobby-z"));
        return new Location(world, x, y, z);
    }
}
