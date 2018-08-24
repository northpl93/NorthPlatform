package pl.arieals.api.minigame.server.gamehost.arena.world;

import static com.google.common.base.Preconditions.checkState;


import javax.xml.bind.JAXB;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.deathmatch.FightStartCountdown;
import pl.arieals.api.minigame.server.gamehost.deathmatch.IFightManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.MapSwitchedEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchLoadedEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.deathmatch.DeathMatchPrepareEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.MapTemplate;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.arieals.api.minigame.shared.api.cfg.DeathMatchConfig;
import pl.arieals.api.minigame.shared.api.cfg.GameMapConfig;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.world.IWorldLoadCallback;
import pl.north93.zgame.api.bukkit.world.IWorldManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

@Slf4j
public class DeathMatch
{
    private final GameHostManager     manager;
    private final LocalArena          arena;
    private       DeathMatchState     state; // stan deathmatchu
    private       FightStartCountdown fightStart; // task startujacy walke
    @Inject
    private       BukkitApiCore       apiCore;

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
        return this.arena.getWorld().getName() + "_deathmatch";
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
        checkState(this.arena.getGamePhase() == GamePhase.STARTED, "Arena must be in STARTED gamephase");

        final DeathMatchPrepareEvent event = this.apiCore.callEvent(new DeathMatchPrepareEvent(this.arena));
        if (event.isCancelled())
        {
            return;
        }

        this.state = DeathMatchState.LOADING;
        final File templateFile = new File(this.manager.getMapTemplateManager().getTemplatesDirectory(), this.getConfig().getTemplateName());
        final MapTemplate template = this.loadTemplate(templateFile);

        final ArenaWorld arenaWorld = this.arena.getWorld();
        final IWorldManager worldManager = this.manager.getWorldManager();

        log.info("Switching arena " + this.arena.getId() + " to death match mode!");

        final File templateDir = template.getMapDirectory();
        worldManager.copyWorld(this.getDeathMathWorldName(), templateDir);
        final IWorldLoadCallback loadCallback = worldManager.loadWorld(this.getDeathMathWorldName(), true, true);

        loadCallback.onComplete(world ->
        {
            if (this.arena.getGamePhase() != GamePhase.STARTED || this.state != DeathMatchState.LOADING)
            {
                log.info("Death match arena loaded, but arena is in gamephase " + this.arena.getGamePhase() + "! Unloading that world...");
                worldManager.unloadAndDeleteWorld(this.getDeathMathWorldName());
                return;
            }

            final World oldWorld = arenaWorld.getCurrentWorld();

            log.info("Death match arena loaded successfully");
            this.state = DeathMatchState.STARTED;

            arenaWorld.switchMap(template, world);
            this.apiCore.callEvent(new MapSwitchedEvent(this.arena, MapSwitchedEvent.MapSwitchReason.DEATH_MATCH));
            this.apiCore.callEvent(new DeathMatchLoadedEvent(this.arena, oldWorld, world));

            // usuwamy poprzedni-normalny swiat areny
            worldManager.unloadAndDeleteWorld(arenaWorld.getName());

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
        else if (this.state == DeathMatchState.STARTED)
        {
            log.info("Removing death match world for arena " + this.arena.getId());
            this.manager.getWorldManager().unloadAndDeleteWorld(this.getDeathMathWorldName());
        }
        this.state = DeathMatchState.NOT_STARTED;
        this.fightStart = null;
    }

    private MapTemplate loadTemplate(final File dir)
    {
        return new MapTemplate(dir.getName(), dir, JAXB.unmarshal(new File(dir, "mapconfig.xml"), GameMapConfig.class));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("state", this.state).toString();
    }
}
