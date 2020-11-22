package pl.north93.northplatform.api.minigame.server.gamehost;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import org.spigotmc.SneakyThrow;
import org.spigotmc.SpigotConfig;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.BukkitHostConnector;
import pl.north93.northplatform.api.bukkit.world.IWorldManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.event.IEventManager;
import pl.north93.northplatform.api.global.utils.JaxbUtils;
import pl.north93.northplatform.api.global.utils.exceptions.ConfigurationException;
import pl.north93.northplatform.api.minigame.server.IServerManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.server.DestroyGameHostServerEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.server.InitializeGameHostServerEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.region.impl.RegionManagerImpl;
import pl.north93.northplatform.api.minigame.server.gamehost.world.IMapTemplateManager;
import pl.north93.northplatform.api.minigame.server.gamehost.world.impl.MapTemplateManager;
import pl.north93.northplatform.api.minigame.shared.api.LobbyMode;
import pl.north93.northplatform.api.minigame.shared.api.arena.netevent.IArenaNetEvent;
import pl.north93.northplatform.api.minigame.shared.api.cfg.MiniGameConfig;
import pl.north93.northplatform.api.minigame.shared.api.hub.IHubServer;

@Slf4j
public class GameHostManager implements IServerManager
{
    @Inject
    private IEventManager eventManager;
    @Inject
    private IWorldManager worldManager;
    @Inject
    private BukkitHostConnector hostConnector;
    @Inject
    private GameHostHubsManager gameHostHubsManager;
    private final RegionManagerImpl regionManager = new RegionManagerImpl();
    private final MapTemplateManager mapTemplateManager = new MapTemplateManager();
    private MiniGameConfig miniGameConfig;

    @Bean
    private GameHostManager()
    {
    }

    @Override
    public void start()
    {
        SpigotConfig.config.set("verbose", false); // disable map-loading spam

        this.loadConfig();
        this.loadMapTemplates();

        this.hostConnector.callEvent(new InitializeGameHostServerEvent());
    }
    
    @Override
    public void stop()
    {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("")); // prevent errors (especially in testing environment)

        this.hostConnector.callEvent(new DestroyGameHostServerEvent());
    }

    @Override
    public UUID getServerId()
    {
        return this.hostConnector.getServerId();
    }

    @Override
    public void tpToHub(final Iterable<? extends Player> players, final String hubId)
    {
        this.gameHostHubsManager.tpToHub(players, hubId);
    }

    @Override
    public void tpToHub(final Iterable<? extends Player> players, final IHubServer hubServer, final String hubId)
    {
        this.gameHostHubsManager.tpToHub(players, hubServer, hubId);
    }

    public <T extends Event> T callBukkitEvent(final T bukkitEvent)
    {
        return this.hostConnector.callEvent(bukkitEvent);
    }

    public void publishArenaEvent(final IArenaNetEvent event)
    {
        this.eventManager.callEvent(event);
    }

    /**
     * Konfiguracja minigry uruchamianej na tym serwerze.
     * @return konfiguracja minigry.
     */
    public MiniGameConfig getMiniGameConfig()
    {
        return this.miniGameConfig;
    }

    /**
     * Zwraca katalog przechowujący dostępne mapy dla minigry.
     * @return katalog z mapami.
     */
    public File getWorldTemplatesDir()
    {
        return this.mapTemplateManager.getTemplatesDirectory();
    }

    public IWorldManager getWorldManager()
    {
        return this.worldManager;
    }
    
    public IMapTemplateManager getMapTemplateManager()
    {
        return this.mapTemplateManager;
    }

    public RegionManagerImpl getRegionManager()
    {
        return this.regionManager;
    }

    private void loadConfig()
    {
        this.miniGameConfig = JaxbUtils.unmarshal(this.hostConnector.getFile("minigame.xml"), MiniGameConfig.class);
        this.validateConfig();
    }
    
    private void validateConfig()
    {
        if (this.miniGameConfig.getMapVoting().getEnabled() && this.miniGameConfig.getLobbyMode() != LobbyMode.EXTERNAL)
        {
            throw new ConfigurationException("Map voting can be only enabled when lobby mode is EXTERNAL.");
        }
    }
    
    private void loadMapTemplates()
    {
        try
        {
            mapTemplateManager.setTemplatesDirectory(new File(miniGameConfig.getMapsDirectory()));
            mapTemplateManager.loadTemplatesFromDirectory();
            log.info("Loaded " + mapTemplateManager.getAllTemplates().size() + " maps templates!");
        }
        catch ( Throwable e )
        {
            SneakyThrow.sneaky(e);
        }
    }
}
