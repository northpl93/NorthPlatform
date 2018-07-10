package pl.arieals.api.minigame.server.gamehost;

import javax.xml.bind.JAXB;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import org.spigotmc.SneakyThrow;
import org.spigotmc.SpigotConfig;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.arieals.api.minigame.server.gamehost.region.impl.RegionManagerImpl;
import pl.arieals.api.minigame.server.gamehost.world.IMapTemplateManager;
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.arieals.api.minigame.server.gamehost.world.impl.MapTemplateManager;
import pl.arieals.api.minigame.server.gamehost.world.impl.WorldManager;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.netevent.IArenaNetEvent;
import pl.arieals.api.minigame.shared.api.cfg.MiniGameConfig;
import pl.arieals.api.minigame.shared.api.hub.IHubServer;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.arieals.api.minigame.shared.api.status.InGameStatus;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.exceptions.ConfigurationException;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;

public class GameHostManager implements IServerManager
{
    @Inject
    private BukkitApiCore       apiCore;
    @Inject
    private IRpcManager         rpcManager;
    @Inject
    private IEventManager       eventManager;
    private GameHostHubsManager gameHostHubsManager = new GameHostHubsManager();
    private LocalArenaManager   arenaManager = new LocalArenaManager();
    private WorldManager        worldManager = new WorldManager();
    private RegionManagerImpl   regionManager = new RegionManagerImpl();
    private MapTemplateManager  mapTemplateManager = new MapTemplateManager();
    private MiniGameConfig      miniGameConfig;

    @Override
    public void start()
    {
        SpigotConfig.config.set("verbose", false); // disable map-loading spam

        this.loadConfig();
        this.loadMapTemplates();

        this.rpcManager.addRpcImplementation(IGameHostRpc.class, new GameHostRpcImpl(this));

        new MiniGameApi(); // inicjuje zmienne w klasie i statyczną INSTANCE

        final BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(this.apiCore.getPluginMain(), this::createArenas);
    }

    private void createArenas()
    {
        for (int i = 0; i < this.miniGameConfig.getArenas(); i++) // create arenas.
        {
            this.arenaManager.createArena();
        }
    }
    
    @Override
    public void stop()
    {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("")); // prevent errors (especially in testing environment)
        this.arenaManager.removeArenas();
    }

    @Override
    public UUID getServerId()
    {
        return this.apiCore.getServerId();
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

    @Override
    public IPlayerStatus getLocation(final Player player)
    {
        final UUID arenaId = this.arenaManager.getArenaAssociatedWith(player.getUniqueId()).map(IArena::getId).orElse(null);
        return new InGameStatus(this.apiCore.getServerId(), arenaId, this.miniGameConfig.getGameIdentity());
    }

    /**
     * Zwraca obiekt pluginu API.
     * @return obiekt pluginu API.
     */
    public JavaPlugin getPlugin()
    {
        return this.apiCore.getPluginMain();
    }

    public BukkitApiCore getApiCore()
    {
        return this.apiCore;
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

    public LocalArenaManager getArenaManager()
    {
        return this.arenaManager;
    }

    public RegionManagerImpl getRegionManager()
    {
        return this.regionManager;
    }

    public void publishArenaEvent(final IArenaNetEvent event)
    {
        this.eventManager.callEvent(event);
    }

    private void loadConfig()
    {
        this.miniGameConfig = JAXB.unmarshal(this.apiCore.getFile("minigame.xml"), MiniGameConfig.class);
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
            apiCore.getLogger().info("Loaded " + mapTemplateManager.getAllTemplates().size() + " maps templates!");
        }
        catch ( Throwable e )
        {
            SneakyThrow.sneaky(e);
        }
    }
}
