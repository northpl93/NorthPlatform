package pl.north93.northplatform.api.minigame.server.gamehost.arena.world;

import static com.google.common.base.Preconditions.checkState;


import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.utils.ISyncCallback;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.utils.JaxbUtils;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.deathmatch.FightStartCountdown;
import pl.north93.northplatform.api.minigame.server.gamehost.deathmatch.IFightManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.MapSwitchedEvent.MapSwitchReason;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchLoadedEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchPrepareEvent;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.MapTemplate;
import pl.north93.northplatform.api.minigame.shared.api.arena.DeathMatchState;
import pl.north93.northplatform.api.minigame.shared.api.cfg.DeathMatchConfig;
import pl.north93.northplatform.api.minigame.shared.api.cfg.GameMapConfig;
import pl.north93.northplatform.api.minigame.shared.api.utils.InvalidGamePhaseException;

@Slf4j
public class DeathMatch
{
    @Inject
    private IBukkitServerManager serverManager;
    private final GameHostManager manager;
    private final LocalArena arena;
    private DeathMatchState state; // stan deathmatchu
    private FightStartCountdown fightStart; // task startujacy walke

    public DeathMatch(final GameHostManager manager, final LocalArena arena)
    {
        this.manager = manager;
        this.arena = arena;
        this.state = DeathMatchState.NOT_STARTED;
    }

    public DeathMatchState getState()
    {
        return this.state;
    }

    public String getDeathMathWorldName()
    {
        return this.arena.getWorld().getDefaultWorldName() + "_deathmatch";
    }

    public DeathMatchConfig getConfig()
    {
        return this.manager.getMiniGameConfig().getDeathMatch();
    }

    /**
     * Zwraca lokalizacje startowa na arenie do deathmatchu
     * gdzie beda teleportowani gracze po zaladowaniu areny.
     * @return spawn areny do deathmatchu.
     */
    public Location getArenaSpawn()
    {
        final ArenaWorld world = this.arena.getWorld();

        final double x = Double.valueOf(world.getProperty("loc-x"));
        final double y = Double.valueOf(world.getProperty("loc-y"));
        final double z = Double.valueOf(world.getProperty("loc-z"));
        return new Location(world.getCurrentWorld(), x, y, z);
    }

    /**
     * Zwraca prawde jesli arena deathmatchu jest aktywna i PvP zostalo
     * na niej wlaczone.
     * @return czy walka na arenie wystartowala.
     */
    public boolean isFightActive()
    {
        return this.state.isActive() && this.fightStart != null && this.fightStart.isFightStarted();
    }

    /**
     * Zwraca klase zarządzająca startem walki.
     * @return klasa zarządzająca stanem walki.
     */
    public IFightManager getFightManager()
    {
        return this.fightStart;
    }

    /**
     * Aktywuje deathmatch na tej arenie.
     * <p>
     * Aby mozna bylo uruchomic deathmatch musza zostac spelnione
     * nastepujace warunki:
     * <ul>
     *     <li>Deathmatch musi byc wlaczony w configu minigry.
     *     <li>Arena musi byc w etapie gry {@link GamePhase#STARTED}.
     *     <li>Deathmatch nie mogl byc wczesniej uruchomiony.
     */
    public void activateDeathMatch()
    {
        checkState(this.getConfig().getEnabled(), "Death match is disabled in this minigame!");
        checkState(this.state == DeathMatchState.NOT_STARTED, "Death match already started!");
        InvalidGamePhaseException.checkGamePhase(this.arena.getGamePhase(), GamePhase.STARTED);

        final DeathMatchPrepareEvent event = this.serverManager.callEvent(new DeathMatchPrepareEvent(this.arena));
        if (event.isCancelled())
        {
            return;
        }

        this.state = DeathMatchState.LOADING;
        final File templateFile = new File(this.manager.getMapTemplateManager().getTemplatesDirectory(), this.getConfig().getTemplateName());
        final MapTemplate template = this.loadTemplate(templateFile);

        log.info("Switching arena {} to death match mode!", this.arena.getId());

        final ArenaWorld arenaWorld = this.arena.getWorld();
        final World oldWorld = arenaWorld.getCurrentWorld();

        final ISyncCallback setMapCallback = arenaWorld.setActiveMap(template, this.getDeathMathWorldName(), MapSwitchReason.DEATH_MATCH);
        setMapCallback.onComplete(() ->
        {
            if (this.state != DeathMatchState.LOADING)
            {
                log.info("Deathmatch is in invalid state: {} after successful map load", this.state);
                return;
            }

            log.info("Death match arena loaded successfully");
            this.state = DeathMatchState.STARTED;

            final World newWorld = this.arena.getWorld().getCurrentWorld();
            this.serverManager.callEvent(new DeathMatchLoadedEvent(this.arena, oldWorld, newWorld));

            this.fightStart = new FightStartCountdown(this.arena);
            // task zostanie automatycznie anulowany/usuniety gdy arena sie skonczy
            this.arena.getScheduler().runAbstractCountdown(this.fightStart, 20);
        });
    }

    /**
     * Resetuje stan deathmatchu po zakonczeniu areny i usuwa swiat deathmatchu.
     */
    public void resetState()
    {
        if (this.state == DeathMatchState.LOADING)
        {
            log.info("Reset state called while death match arena is loading. Loading will be canceled later.");
        }
        this.state = DeathMatchState.NOT_STARTED;
        this.fightStart = null;
    }

    private MapTemplate loadTemplate(final File dir)
    {
        return new MapTemplate(dir.getName(), dir, JaxbUtils.unmarshal(new File(dir, "mapconfig.xml"), GameMapConfig.class));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("state", this.state).toString();
    }
}
