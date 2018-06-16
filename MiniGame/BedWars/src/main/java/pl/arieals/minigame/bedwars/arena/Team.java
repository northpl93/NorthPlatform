package pl.arieals.minigame.bedwars.arena;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;


import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.arieals.minigame.bedwars.cfg.BwTeamConfig;
import pl.arieals.minigame.bedwars.event.BedDestroyedEvent;
import pl.arieals.minigame.bedwars.event.TeamEliminatedEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class Team
{
    private final LocalArena arena;
    private final BwTeamConfig config;
    private Set<Player> players;
    private Cuboid      teamArena; // teren obejmujacy cala baze
    private Cuboid      healArena; // teren wewnatrz budynku bazy
    private Location    spawn;
    private Location    bedLocation;
    private boolean     isBedAlive;
    private boolean     isAlreadyEliminated;
    private Upgrades    upgrades;

    public Team(final LocalArena arena, final BwTeamConfig config)
    {
        this.arena = arena;
        this.config = config;

        final World currentWorld = arena.getWorld().getCurrentWorld();
        this.players = new HashSet<>();
        this.teamArena = config.getTeamRegion().toCuboid(currentWorld);
        this.healArena = config.getHealRegion().toCuboid(currentWorld);
        this.spawn = config.getSpawnLocation().toBukkit(currentWorld);
        this.bedLocation = config.getBedLocation().toBukkit(currentWorld);
        this.isBedAlive = true;
        this.isAlreadyEliminated = false;
        this.upgrades = new Upgrades(arena, this);
    }

    /**
     * Zwraca arene powiazana z tym teamem bedwarsow.
     * @return arena do ktorej nalezy ten team.
     */
    public LocalArena getArena()
    {
        return this.arena;
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
        return this.getColor().name().toLowerCase(Locale.ROOT);
    }

    public Set<Player> getPlayers()
    {
        return this.players;
    }

    /**
     * Zwraca graczy zyjacych (BEZ wyeliminowanych i o czekujacych na respawn)
     * @return gracze zyjacy/grajacy.
     */
    public Set<Player> getAlivePlayers()
    {
        return this.players.stream().filter(player -> getPlayerStatus(player) == PlayerStatus.PLAYING).collect(Collectors.toSet());
    }

    /**
     * Zwraca graczy zyjacych i oczekujacych na respawn (ALE NIE wyeliminowanych)
     * @return gracze zyjacy i czekajacy na respawn.
     */
    public Set<Player> getNotEliminatedPlayers()
    {
        return this.players.stream().filter(player -> ! getPlayerData(player, BedWarsPlayer.class).isEliminated()).collect(Collectors.toSet());
    }

    /**
     * @return liczba dodatkowych zyc posiadanych przez graczy.
     */
    public int countAdditionalLives()
    {
        return this.players.stream().mapToInt(player -> getPlayerData(player, BedWarsPlayer.class).getLives()).sum();
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
     * Niszczy łózko danej drużyny, używane w tasku do niszczenia po pewnym czasie.
     */
    public void destroyBed(final boolean silent)
    {
        if (! this.isBedAlive())
        {
            return;
        }

        this.setBedAlive(false);
        final Block bedBlock = this.getBedLocation().getBlock();
        bedBlock.setType(Material.AIR); // usuwamy klasyczna, wolna metoda zeby znikly obydwie czesci lozka.

        final BukkitApiCore apiCore = this.arena.getGameHostManager().getApiCore();
        apiCore.callEvent(new BedDestroyedEvent(this.arena, null, bedBlock, this, silent));
    }

    /**
     * Sprawdza czy ten team zostal juz wywliminowany.
     * Uwaga, to pobiera zawartosc zmiennej, nie sprawdza
     * stanu faktycznego.
     * @return czy team zostal wyeliminowany.
     */
    public boolean isEliminated()
    {
        return this.isAlreadyEliminated;
    }

    /**
     * Sprawdza czy team pelnia warunki do eliminacji i ewentualnie
     * eliminuje wywolujac tym samym event.
     */
    public void checkEliminated()
    {
        if (this.isAlreadyEliminated)
        {
            return;
        }

        if (! this.isTeamAlive())
        {
            this.forceEliminate();
        }
    }

    /**
     * Sprawdza czy dany team jest zywy, tzn.
     * czy ma lozku lub przynajmniej jednego zywego/respawnujacego sie gracza
     * @return czy team jest zywy.
     */
    private boolean isTeamAlive()
    {
        if (this.isBedAlive)
        {
            return true;
        }
        for (final Player player : this.players)
        {
            if (! player.isOnline())
            {
                // jesli gracz jest offline to nie uwzgledniamy go przy sprawdzaniu eliminacji teamu.
                continue;
            }

            final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
            if (playerData != null && ! playerData.isEliminated())
            {
                return true;
            }
        }
        return false;
    }

    private void forceEliminate()
    {
        if (this.isAlreadyEliminated)
        {
            return;
        }
        this.isAlreadyEliminated = true;
        Bukkit.getPluginManager().callEvent(new TeamEliminatedEvent(this.arena, this));
    }

    public Upgrades getUpgrades()
    {
        return this.upgrades;
    }
}
