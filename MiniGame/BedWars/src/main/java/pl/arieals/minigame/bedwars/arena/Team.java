package pl.arieals.minigame.bedwars.arena;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.cfg.BwTeamConfig;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class Team
{
    private final BwTeamConfig config;
    private Set<Player> players;
    private Cuboid      teamArena;
    private Cuboid      healArena;
    private Location    spawn;
    private Location    bedLocation;
    private boolean     isBedAlive;
    private Upgrades    upgrades;

    public Team(final LocalArena arena, final BwTeamConfig config)
    {
        this.config = config;

        final World currentWorld = arena.getWorld().getCurrentWorld();
        this.players = new HashSet<>();
        this.teamArena = config.getTeamRegion().toCuboid(currentWorld);
        this.healArena = config.getHealRegion().toCuboid(currentWorld);
        this.spawn = config.getSpawnLocation().toBukkit(currentWorld);
        this.bedLocation = config.getBedLocation().toBukkit(currentWorld);
        this.isBedAlive = true;
        this.upgrades = new Upgrades(arena, this);
    }

    public BwTeamConfig getConfig()
    {
        return this.config;
    }

    /**
     * Ta cyfra jest uzywana do sortowania scoreboardu
     * wedlug configu.
     * @return kolejnosc na scoreboardzie.
     */
    public int getScoreboardOrder()
    {
        return this.config.getScoreboardOrder();
    }

    /**
     * Zwraca kolor danego teamu jako Bukkitowy {@code ChatColor}.
     * @return kolor teamu.
     */
    public ChatColor getColor()
    {
        return this.config.getColor();
    }

    /**
     * Zwraca wewnętrzną nazwę tego teamu.
     * Jest to nazwa koloru bukkitowego małymi literami.
     * np. red lub green.
     * @return wewnętrzna naza teamu.
     */
    public String getName()
    {
        return this.getColor().name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Zwraca literę reprezentującą dany kolor w Bukkicie
     * np. dla RED zwróci c.
     * @see ChatColor
     * @return litera koloru danego teamu.
     */
    public char getColorChar()
    {
        return this.getColor().getChar();
    }

    public Set<Player> getPlayers()
    {
        return this.players;
    }

    public Set<Player> getAlivePlayers()
    {
        return this.players.stream().filter(player -> getPlayerData(player, BedWarsPlayer.class).isAlive()).collect(Collectors.toSet());
    }

    public Cuboid getTeamArena()
    {
        return this.teamArena;
    }

    public Cuboid getHealArena()
    {
        return this.healArena;
    }

    public Location getSpawn()
    {
        return this.spawn;
    }

    public Location getBedLocation()
    {
        return this.bedLocation;
    }

    /**
     * Sprawdza czy lozko danego teamu nadal stoi.
     * @return czy lozko nadal istnieje.
     */
    public boolean isBedAlive()
    {
        return this.isBedAlive;
    }

    public void setBedAlive(final boolean bedAlive)
    {
        this.isBedAlive = bedAlive;
    }

    /**
     * Sprawdza czy dany team jest zywy, tzn.
     * czy ma lozku lub przynajmniej jednego zywego gracza
     * @return czy team jest zywy.
     */
    public boolean isTeamAlive()
    {
        return this.isBedAlive || ! this.getAlivePlayers().isEmpty();
    }

    public Upgrades getUpgrades()
    {
        return this.upgrades;
    }
}
