package pl.north93.northplatform.minigame.bedwars.arena;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.region.Cuboid;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.cfg.BwTeamConfig;
import pl.north93.northplatform.minigame.bedwars.event.BedDestroyedEvent;
import pl.north93.northplatform.minigame.bedwars.event.TeamEliminatedEvent;

public class Team
{
    private final LocalArena arena;
    private final BwTeamConfig config;
    private final Set<BedWarsPlayer> players;
    private Cuboid teamArena; // teren obejmujacy cala baze
    private Cuboid healArena; // teren wewnatrz budynku bazy
    private Location spawn;
    private Location bedLocation;
    private boolean isBedAlive;
    private boolean isAlreadyEliminated;
    private Upgrades upgrades;

    public Team(final LocalArena arena, final BwTeamConfig config)
    {
        this.arena = arena;
        this.config = config;
        this.players = new HashSet<>();

        final World currentWorld = arena.getWorld().getCurrentWorld();
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

    /**
     * Zwraca listę wszystkich BedWarsPlayer przypisanych do tej drużyny.
     * Mogą oni być już offline jeśli opuścili serwer minigry, ale
     * mogą wrócić jeśli system reconnectu zezwoli.
     *
     * @return Lista wszystkich graczy w drużynie.
     */
    public Set<BedWarsPlayer> getPlayers()
    {
        return this.players;
    }

    public Stream<INorthPlayer> getBukkitPlayersAsStream()
    {
        return this.players.stream().map(BedWarsPlayer::getBukkitPlayer);
    }

    public Set<INorthPlayer> getBukkitPlayers()
    {
        return this.getBukkitPlayersAsStream().collect(Collectors.toSet());
    }

    /**
     * Zwraca graczy zyjacych i oczekujacych na respawn (ALE NIE wyeliminowanych)
     * @return gracze zyjacy i czekajacy na respawn.
     */
    public Set<Player> getNotEliminatedPlayers()
    {
        return this.players.stream()
                           .filter(player -> ! player.isEliminated())
                           .map(BedWarsPlayer::getBukkitPlayer)
                           .collect(Collectors.toSet());
    }

    /**
     * @return liczba dodatkowych zyc posiadanych przez graczy.
     */
    public int countAdditionalLives()
    {
        return this.players.stream().mapToInt(BedWarsPlayer::getLives).sum();
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
        this.doBedBlockDestroy(bedBlock);

        final GameHostManager gameHostManager = this.arena.getGameHostManager();
        gameHostManager.callBukkitEvent(new BedDestroyedEvent(this.arena, null, bedBlock, this, silent));
    }

    // usuwamy klasyczna, wolna metoda zeby znikly obydwie czesci lozka.
    // zawsze musimy usuac glówna (head) czesc lozka, zeby itemek nie wydropil
    private void doBedBlockDestroy(final Block bedBlock)
    {
        final Bed bed = (Bed) bedBlock.getState().getData();
        if (bed.isHeadOfBed())
        {
            bedBlock.setType(Material.AIR);
        }
        else
        {
            final Block relative = bedBlock.getRelative(bed.getFacing());
            relative.setType(Material.AIR);
        }
    }

    /**
     * @return True jeśli w teamie jest conajmniej jeden żywy gracz online.
     */
    public boolean isAnyPlayerAlive()
    {
        for (final BedWarsPlayer playerData : this.players)
        {
            if (playerData.isOffline())
            {
                // jesli gracz jest offline to nie uwzgledniamy go przy sprawdzaniu
                continue;
            }

            if (! playerData.isEliminated())
            {
                return true;
            }
        }

        return false;
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
        if (this.isAlreadyEliminated || this.isTeamAlive())
        {
            return;
        }

        this.forceEliminate();
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

        return this.isAnyPlayerAlive();
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
