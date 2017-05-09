package pl.arieals.api.minigame.server.gamehost;

import javax.xml.bind.JAXB;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import org.spigotmc.SneakyThrow;
import org.spigotmc.SpigotConfig;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.arieals.api.minigame.server.gamehost.listener.ArenaEndListener;
import pl.arieals.api.minigame.server.gamehost.listener.ArenaInitListener;
import pl.arieals.api.minigame.server.gamehost.listener.GameStartListener;
import pl.arieals.api.minigame.server.gamehost.listener.GameStartScheduler;
import pl.arieals.api.minigame.server.gamehost.listener.PlayerListener;
import pl.arieals.api.minigame.server.gamehost.lobby.ILobbyManager;
import pl.arieals.api.minigame.server.gamehost.lobby.external.ExternalLobby;
import pl.arieals.api.minigame.server.gamehost.lobby.integrated.IntegratedLobby;
import pl.arieals.api.minigame.server.gamehost.region.impl.RegionManagerImpl;
import pl.arieals.api.minigame.server.gamehost.world.IMapTemplateManager;
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.arieals.api.minigame.server.gamehost.world.impl.MapTemplateManager;
import pl.arieals.api.minigame.server.gamehost.world.impl.WorldManager;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.MiniGameConfig;
import pl.arieals.api.minigame.shared.api.arena.netevent.IArenaNetEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectNewInstance;
import pl.north93.zgame.api.global.exceptions.ConfigurationException;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;

public class GameHostManager implements IServerManager
{
    private BukkitApiCore     apiCore;
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
    private TemplateManager   msgPack;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager       rpcManager;
    @InjectComponent("API.Database.Redis.Subscriber")
    private RedisSubscriber   subscriber;
    @InjectNewInstance
    private LocalArenaManager arenaManager;
    @InjectNewInstance
    private WorldManager      worldManager;
    @InjectNewInstance
    private RegionManagerImpl regionManager;
    @InjectNewInstance
    private MapTemplateManager mapTemplateManager;
    private ILobbyManager     lobbyManager;
    private MiniGameConfig    miniGameConfig;

    @Override
    public void start()
    {
        SpigotConfig.config.set("verbose", false); // disable map-loading spam

        this.loadConfig();

        this.rpcManager.addRpcImplementation(IGameHostRpc.class, new GameHostRpcImpl(this));
        this.lobbyManager = (this.miniGameConfig.getLobbyMode() == LobbyMode.EXTERNAL) ? new ExternalLobby() : new IntegratedLobby();

        this.apiCore.registerEvents(
                new PlayerListener(), // dodaje graczy do aren
                new ArenaInitListener(), // inicjuje arene po dodaniu/zakonczeniu poprzedniej gry
                new GameStartScheduler(), // planuje rozpoczecie gry gdy arena jest w lobby
                new GameStartListener(), // inicjuje gre po starcie
                new ArenaEndListener()); // pilnuje by arena nie stala pusta i wykonuje czynnosci koncowe
        
        this.loadMapTemplates();
        
        new MiniGameApi(); // inicjuje zmienne w klasie i statyczną INSTANCE

        for (int i = 0; i < 4; i++) // create 4 arenas.
        {
            this.arenaManager.createArena();
        }

        /*final GameMapConfig config = new GameMapConfig();
        config.setDisplayName("test");
        config.setEnabled(true);
        config.getProperties().put("klucz1", "wartosc1");
        config.getProperties().put("klucz2", "wartosc2");
        JAXB.marshal(config, new File("testy.xml"));*/
    }

    @Override
    public void stop()
    {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("")); // prevent errors (especially in testing environment)
        this.arenaManager.removeArenas();
    }

    /**
     * Zwraca obiekt pluginu API.
     * @return obiekt pluginu API.
     */
    public JavaPlugin getPlugin()
    {
        return this.apiCore.getPluginMain();
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
        return new File("game_worlds");
    }

    public IWorldManager getWorldManager()
    {
        return this.worldManager;
    }

    public ILobbyManager getLobbyManager()
    {
        return this.lobbyManager;
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

    public <T> T getPlayerData(final Player player, final Class<T> clazz)
    {
        //noinspection unchecked
        return (T) player.getMetadata(clazz.getName()).get(0).value();
    }

    public void setPlayerData(final Player player, final Object data)
    {
        player.setMetadata(data.getClass().getName(), new FixedMetadataValue(this.apiCore.getPluginMain(), data));
    }

    public void publishArenaEvent(final IArenaNetEvent event)
    {
        final byte[] bytes = this.msgPack.serialize(IArenaNetEvent.class, event);
        this.subscriber.publish("minigames:arena_event", bytes);
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
        }
        catch ( Throwable e )
        {
            SneakyThrow.sneaky(e);
        }
    }
}
