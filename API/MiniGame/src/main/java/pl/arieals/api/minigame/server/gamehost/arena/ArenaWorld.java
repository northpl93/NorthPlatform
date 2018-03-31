package pl.arieals.api.minigame.server.gamehost.arena;

import static pl.arieals.api.minigame.shared.api.utils.InvalidGamePhaseException.checkGamePhase;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.MapSwitchedEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.MapSwitchedEvent.MapSwitchReason;
import pl.arieals.api.minigame.server.gamehost.world.ILoadingProgress;
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.arieals.api.minigame.shared.api.cfg.GameMapConfig;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.MapTemplate;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaDataChangedNetEvent;
import pl.north93.zgame.api.bukkit.utils.ISyncCallback;
import pl.north93.zgame.api.bukkit.utils.SimpleSyncCallback;

public class ArenaWorld
{
    private final GameHostManager gameHostManager;
    private final LocalArena      arena;
    private MapTemplate           currentMapTemplate;
    private World                 currentWorld;
    private ILoadingProgress      progress;

    public ArenaWorld(final GameHostManager gameHostManager, final LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.arena = arena;;
    }

    public File getWorldDirectory()
    {
        return new File(Bukkit.getWorldContainer(), this.getName());
    }

    public File getResource(final String name)
    {
        return new File(this.getWorldDirectory(), name);
    }

    public InputStream getResourceAsStream(final String name)
    {
        try
        {
            return FileUtils.openInputStream(this.getResource(name));
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Exception in getResourceAsStream(" + name + ")", e);
        }
    }

    public World getCurrentWorld()
    {
        return this.currentWorld;
    }

    public String getName()
    {
        return "arena_" + this.arena.getId();
    }

    public MapTemplate getCurrentMapTemplate()
    {
        return this.currentMapTemplate;
    }
    
    public GameMapConfig getCurrentMapConfig()
    {
        return this.currentMapTemplate.getMapConfig();
    }
    
    public String getProperty(final String key)
    {
        return this.currentMapTemplate.getMapConfig().getProperties().get(key);
    }

    /**
     * Sprawdza czy mapa jest załadowana.
     * @return czy mapa jest załadowana.
     */
    public boolean isReady()
    {
        return this.currentWorld != null && this.progress != null && this.progress.isComplete();
    }

    /**
     * Wczytuje wybraną mapę dla tej areny.
     * Może być wykonane tylko gdy arena znajduje się w GamePhase LOBBY.
     * @param template Mapa do załadowania.
     * @return callback który informuje o pomyślnym załadowaniu mapy.
     */
    public ISyncCallback setActiveMap(final MapTemplate template)
    {
        if (this.arena.getLobbyMode() == LobbyMode.EXTERNAL)
        {
            checkGamePhase(this.arena.getGamePhase(), GamePhase.LOBBY);
        }
        else
        {
            checkGamePhase(this.arena.getGamePhase(), GamePhase.INITIALISING);
        }

        final IWorldManager worldManager = this.gameHostManager.getWorldManager();

        final File templateDir = template.getMapDirectory();
        final ILoadingProgress progress = worldManager.regenWorld(this.getName(), templateDir, template.getMapConfig().getChunks());

        this.switchMap(template, progress.getWorld(), progress);

        final SimpleSyncCallback callback = new SimpleSyncCallback();
        progress.onComplete(() ->
        {
            this.gameHostManager.publishArenaEvent(new ArenaDataChangedNetEvent(this.arena.getId(), this.arena.getMiniGame(), template.getName(), this.arena.getGamePhase(), this.arena.getPlayers().size()));
            Bukkit.getPluginManager().callEvent(new MapSwitchedEvent(this.arena, MapSwitchReason.ARENA_INITIALISE));
            callback.callComplete();
        });

        return callback;
    }

    /*default*/ void switchMap(final MapTemplate newMapTemplate, final World newWorld, final ILoadingProgress progress)
    {
        this.currentMapTemplate = newMapTemplate;
        this.currentWorld = newWorld;
        this.progress = progress;

        for (final Map.Entry<String, String> gameRule : newMapTemplate.getMapConfig().getGameRules().entrySet())
        {
            newWorld.setGameRuleValue(gameRule.getKey(), gameRule.getValue());
        }
    }

    public boolean delete()
    {
        final IWorldManager worldManager = this.gameHostManager.getWorldManager();
        if (this.arena.getDeathMatch().getState() == DeathMatchState.STARTED)
        {
            return worldManager.clearWorld(this.arena.getDeathMatch().getDeathMathWorldName());
        }
        else
        {
            return worldManager.clearWorld(this.getName());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("world", this.currentWorld).append("progress", this.progress).toString();
    }
}
