package pl.north93.northplatform.api.minigame.server.gamehost.arena.world;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.World;

import org.apache.commons.io.FileUtils;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.utils.ISyncCallback;
import pl.north93.northplatform.api.bukkit.utils.SimpleSyncCallback;
import pl.north93.northplatform.api.bukkit.world.IWorldLoadCallback;
import pl.north93.northplatform.api.bukkit.world.IWorldManager;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.MapSwitchedEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.MapSwitchedEvent.MapSwitchReason;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.LobbyMode;
import pl.north93.northplatform.api.minigame.shared.api.MapTemplate;
import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.StandardArenaMetaData;
import pl.north93.northplatform.api.minigame.shared.api.cfg.GameMapConfig;
import pl.north93.northplatform.api.minigame.shared.api.utils.InvalidGamePhaseException;

@Slf4j
@ToString(of = {"currentWorld"})
public class ArenaWorld
{
    private final GameHostManager gameHostManager;
    private final LocalArena arena;
    private MapTemplate currentMapTemplate;
    private World currentWorld;

    public ArenaWorld(final GameHostManager gameHostManager, final LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.arena = arena;;
    }

    public String getDefaultWorldName()
    {
        return "arena_" + this.arena.getId();
    }

    public File getWorldDirectory()
    {
        return new File(Bukkit.getWorldContainer(), this.currentWorld.getName());
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

    public ISyncCallback setActiveMap(final MapTemplate template, final MapSwitchReason reason)
    {
        return this.setActiveMap(template, this.getDefaultWorldName(), reason);
    }

    /**
     * Changes active map for this arena
     * You can change map when:
     * 1. Arena is in LOBBY and has external lobby
     * 2. Arena is in INITIALISING and has internal lobby
     * 3. Arena is in STARTED.
     *
     * @param template New template to be loaded
     * @param worldName Name of the new world
     * @param reason Reason of the world switch.
     * @return callback which is executed when new map is ready for use.
     */
    public ISyncCallback setActiveMap(final MapTemplate template, final String worldName, final MapSwitchReason reason)
    {
        Preconditions.checkNotNull(template, "New MapTemplate can't be null");

        final LobbyMode lobbyMode = this.arena.getLobbyMode();
        final GamePhase gamePhase = this.arena.getGamePhase();

        if (lobbyMode == LobbyMode.EXTERNAL && gamePhase == GamePhase.LOBBY)
        {
            return this.performOutGameMapSwitch(template, worldName, reason);
        }
        else if (lobbyMode == LobbyMode.INTEGRATED && gamePhase == GamePhase.INITIALISING)
        {
            return this.performOutGameMapSwitch(template, worldName, reason);
        }
        else if (gamePhase == GamePhase.STARTED)
        {
            return this.performInGameMapSwitch(template, worldName, reason);
        }

        throw new InvalidGamePhaseException(gamePhase, null);
    }

    private ISyncCallback performOutGameMapSwitch(final MapTemplate template, final String worldName, final MapSwitchReason reason)
    {
        // delete previous world
        this.deleteCurrentWorld();

        // load new world and set variables in this object
        final IWorldLoadCallback worldLoadCallback = this.copyAndLoadWorld(template, worldName);

        final SimpleSyncCallback mapSwitchCallback = new SimpleSyncCallback();
        worldLoadCallback.onComplete(world ->
        {
            this.updateWorldMetadata(world, template);
            Bukkit.getPluginManager().callEvent(new MapSwitchedEvent(this.arena, false, reason));
            mapSwitchCallback.callComplete();
        });

        return mapSwitchCallback;
    }

    private ISyncCallback performInGameMapSwitch(final MapTemplate template, final String worldName, final MapSwitchReason reason)
    {
        final World worldBeforeSwitch = this.currentWorld;
        final IWorldLoadCallback worldLoadCallback = this.copyAndLoadWorld(template, worldName);

        final SimpleSyncCallback callback = new SimpleSyncCallback();
        worldLoadCallback.onComplete(world ->
        {
            final IWorldManager worldManager = this.gameHostManager.getWorldManager();
            if (this.arena.getGamePhase() != GamePhase.STARTED)
            {
                log.info("Arena {} ended before in game world switch completed. Deleting {}", this.arena.getId(), world.getName());
                worldManager.unloadAndDeleteWorld(world);
                return;
            }

            this.updateWorldMetadata(world, template);
            Bukkit.getPluginManager().callEvent(new MapSwitchedEvent(this.arena, true, reason));
            callback.callComplete();

            log.info("In game world switch on {} completed, removing old world {}", this.arena.getId(), worldBeforeSwitch.getName());
            worldManager.unloadAndDeleteWorld(worldBeforeSwitch);
        });

        return callback;
    }

    private IWorldLoadCallback copyAndLoadWorld(final MapTemplate newMapTemplate, final String worldName)
    {
        final IWorldManager worldManager = this.gameHostManager.getWorldManager();
        log.info("Switching arena {} to new world with name {}", this.arena.getId(), worldName);

        final File templateDir = newMapTemplate.getMapDirectory();
        worldManager.copyWorld(worldName, templateDir);

        return worldManager.loadWorld(worldName, true, true);
    }

    private void updateWorldMetadata(final World newWorld, final MapTemplate newMapTemplate)
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

        log.info("World successfully updated on {}, uploading info to redis...", this.arena.getId());
        this.arena.uploadRemoteData(); // wywola sieciowy event aktualizacji danych areny
    }

    public void deleteCurrentWorld()
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
}
