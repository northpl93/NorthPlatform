package pl.arieals.minigame.bedwars.arena;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.cfg.BedWarsTeamConfig;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class Team
{
    private ChatColor   color;
    private Set<Player> players;
    private Cuboid      teamArena;
    private Location    spawn;
    private boolean     isBedAlive;

    public Team(final LocalArena arena, final BedWarsTeamConfig config)
    {
        this.color = config.getColor();
        this.players = new HashSet<>();
        this.teamArena = config.getTeamRegion().toCuboid(arena.getWorld().getCurrentWorld());
        this.spawn = config.getSpawnLocation().toBukkit(arena.getWorld().getCurrentWorld());
        this.isBedAlive = true;
    }

    public ChatColor getColor()
    {
        return this.color;
    }

    public Set<Player> getPlayers()
    {
        return this.players;
    }

    public Cuboid getTeamArena()
    {
        return this.teamArena;
    }

    public Location getSpawn()
    {
        return this.spawn;
    }

    public boolean isBedAlive()
    {
        return this.isBedAlive;
    }

    public void setBedAlive(final boolean bedAlive)
    {
        this.isBedAlive = bedAlive;
    }
}
