package pl.north93.northplatform.api.minigame.server.gamehost.arena.world;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.World;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.MapSwitchedEvent;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.LobbyMode;
import pl.north93.northplatform.api.minigame.shared.api.MapTemplate;
import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.StandardArenaMetaData;
import pl.north93.northplatform.api.minigame.shared.api.cfg.GameMapConfig;
import pl.north93.northplatform.api.bukkit.utils.ISyncCallback;
import pl.north93.northplatform.api.bukkit.utils.SimpleSyncCallback;
import pl.north93.northplatform.api.bukkit.world.IWorldLoadCallback;
import pl.north93.northplatform.api.bukkit.world.IWorldManager;
import pl.north93.northplatform.api.minigame.shared.api.utils.InvalidGamePhaseException;

public class ArenaWorld
{
    private final GameHostManager gameHostManager;
    private final LocalArena      arena;
    private       MapTemplate     currentMapTemplate;
    private       World           currentWorld;

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
        return this.currentWorld != null;
    }

    /**
     * Wczytuje wybraną mapę dla tej areny.
     * Może być wykonane tylko gdy arena znajduje się w GamePhase LOBBY.
     * @param template Mapa do załadowania.
     * @return callback który informuje o pomyślnym załadowaniu mapy.
     */
    public ISyncCallback setActiveMap(final MapTemplate template)
    {
        Preconditions.checkNotNull(template, "New MapTemplate can't be null");

        if (this.arena.getLobbyMode() == LobbyMode.EXTERNAL)
        {
            InvalidGamePhaseException.checkGamePhase(this.arena.getGamePhase(), GamePhase.LOBBY);
        }
        else
        {
            InvalidGamePhaseException.checkGamePhase(this.arena.getGamePhase(), GamePhase.INITIALISING);
        }

        this.delete(); // delete previous world
        final IWorldManager worldManager = this.gameHostManager.getWorldManager();

        final File templateDir = template.getMapDirectory();
        worldManager.copyWorld(this.getName(), templateDir);

        final IWorldLoadCallback worldLoadCallback = worldManager.loadWorld(this.getName(), true, true);
        this.switchMap(template, worldLoadCallback.getWorld());

        final SimpleSyncCallback callback = new SimpleSyncCallback();
        worldLoadCallback.onComplete(world ->
        {
            this.arena.uploadRemoteData(); // wywola sieciowy event aktualizacji danych areny
            Bukkit.getPluginManager().callEvent(new MapSwitchedEvent(this.arena, MapSwitchedEvent.MapSwitchReason.ARENA_INITIALISE));
            callback.callComplete();
        });

        return callback;
    }

    /*default*/ void switchMap(final MapTemplate newMapTemplate, final World newWorld)
    {
        this.currentMapTemplate = newMapTemplate;
        this.currentWorld = newWorld;

        final RemoteArena remoteArena = this.arena.getAsRemoteArena(); // upload nastąpi w onComplete
        remoteArena.getMetadata().set(StandardArenaMetaData.WORLD_ID, newMapTemplate.getName());
        remoteArena.getMetadata().set(StandardArenaMetaData.WORLD_NAME, newMapTemplate.getDisplayName());

        for (final Map.Entry<String, String> gameRule : newMapTemplate.getMapConfig().getGameRules().entrySet())
        {
            newWorld.setGameRuleValue(gameRule.getKey(), gameRule.getValue());
        }
    }

    public void delete()
    {
        if (this.currentWorld == null)
        {
            return;
        }

        final IWorldManager worldManager = this.gameHostManager.getWorldManager();
        worldManager.unloadAndDeleteWorld(this.currentWorld);

        this.currentMapTemplate = null;
        this.currentWorld = null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("world", this.currentWorld).toString();
    }
}
