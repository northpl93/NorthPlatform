package pl.arieals.api.minigame.server.gamehost;

import javax.xml.bind.JAXB;

import java.io.File;
import java.util.List;

import net.minecraft.server.v1_10_R1.DedicatedPlayerList;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.IPlayerFileData;
import net.minecraft.server.v1_10_R1.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import org.spigotmc.SneakyThrow;
import org.spigotmc.SpigotConfig;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.arieals.api.minigame.server.gamehost.deathmatch.DeathMatchFightListener;
import pl.arieals.api.minigame.server.gamehost.listener.ArenaEndListener;
import pl.arieals.api.minigame.server.gamehost.listener.ArenaInitListener;
import pl.arieals.api.minigame.server.gamehost.deathmatch.DeathMatchStartListener;
import pl.arieals.api.minigame.server.gamehost.listener.GameStartListener;
import pl.arieals.api.minigame.server.gamehost.listener.PlayerListener;
import pl.arieals.api.minigame.server.gamehost.listener.VisibilityListener;
import pl.arieals.api.minigame.server.gamehost.region.impl.RegionManagerImpl;
import pl.arieals.api.minigame.server.gamehost.world.IMapTemplateManager;
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.arieals.api.minigame.server.gamehost.world.impl.MapTemplateManager;
import pl.arieals.api.minigame.server.gamehost.world.impl.WorldManager;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.cfg.MiniGameConfig;
import pl.arieals.api.minigame.shared.api.arena.netevent.IArenaNetEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.exceptions.ConfigurationException;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;

public class GameHostManager implements IServerManager
{
    @Inject
    private BukkitApiCore     apiCore;
    @Inject
    private TemplateManager   msgPack;
    @Inject
    private IRpcManager       rpcManager;
    @Inject
    private RedisSubscriber   subscriber;
    private LocalArenaManager arenaManager = new LocalArenaManager();
    private WorldManager      worldManager = new WorldManager();
    private RegionManagerImpl regionManager = new RegionManagerImpl();
    private MapTemplateManager mapTemplateManager = new MapTemplateManager();
    private MiniGameConfig    miniGameConfig;

    @Override
    public void start()
    {
        SpigotConfig.config.set("verbose", false); // disable map-loading spam
        this.disableSavePlayerData();
        
        this.loadConfig();

        this.rpcManager.addRpcImplementation(IGameHostRpc.class, new GameHostRpcImpl(this));

        this.apiCore.registerEvents(
                new PlayerListener(), // dodaje graczy do aren
                new VisibilityListener(), // zarzadza widocznoscia graczy i czatu
                new ArenaInitListener(), // inicjuje arene po dodaniu/zakonczeniu poprzedniej gry
                new GameStartListener(), // inicjuje gre po starcie
                new DeathMatchStartListener(), // pilnuje starty death matchu
                new DeathMatchFightListener(), // pilnuje walki na deathmatchu
                new ArenaEndListener()); // pilnuje by arena nie stala pusta i wykonuje czynnosci koncowe
        
        this.loadMapTemplates();
        
        new MiniGameApi(); // inicjuje zmienne w klasie i statyczną INSTANCE

        Bukkit.getScheduler().runTask(apiCore.getPluginMain(), this::createArenas);

        /*final GameMapConfig config = new GameMapConfig();
        config.setDisplayName("test");
        config.setEnabled(true);
        config.getProperties().put("klucz1", "wartosc1");
        config.getProperties().put("klucz2", "wartosc2");
        JAXB.marshal(config, new File("testy.xml"));*/
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

    public <T> T getPlayerData(final Player player, final Class<T> clazz)
    {
        final List<MetadataValue> metadata = player.getMetadata(clazz.getName());
        if (metadata.isEmpty())
        {
            return null;
        }
        //noinspection unchecked
        return (T) metadata.get(0).value();
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
            apiCore.getLogger().info("Loaded " + mapTemplateManager.getAllTemplates().size() + " maps templates!");
        }
        catch ( Throwable e )
        {
            SneakyThrow.sneaky(e);
        }
    }
    
    private void disableSavePlayerData()
    {
        DedicatedPlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();
        IPlayerFileData current = playerList.playerFileData;
        
        IPlayerFileData data = new IPlayerFileData()
        {
            
            @Override
            public void save(EntityHuman player)
            {
            }
            
            @Override
            public NBTTagCompound load(EntityHuman player)
            {
                return current.load(player);
            }
            
            @Override
            public String[] getSeenPlayers()
            {
                return current.getSeenPlayers();
            }
        };
        
        playerList.playerFileData = data;
    }
}
