package pl.arieals.api.minigame.server.gamehost;

import java.io.File;

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
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.arieals.api.minigame.server.gamehost.world.impl.WorldManager;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.MiniGame;
import pl.arieals.api.minigame.shared.api.arena.netevent.IArenaNetEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.cfg.ConfigUtils;
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
    private ILobbyManager     lobbyManager;
    private MiniGame          miniGame;

    @Override
    public void start()
    {
        SpigotConfig.config.set("verbose", false); // disable map-loading spam

        this.miniGame = ConfigUtils.loadConfigFile(MiniGame.class, this.apiCore.getFile("minigame.yml"));
        this.validateConfig(this.miniGame);

        this.rpcManager.addRpcImplementation(IGameHostRpc.class, new GameHostRpcImpl(this));
        this.lobbyManager = (this.miniGame.getLobbyMode() == LobbyMode.EXTERNAL) ? new ExternalLobby() : new IntegratedLobby();

        this.apiCore.registerEvents(
                new PlayerListener(), // dodaje graczy do aren
                new ArenaInitListener(), // inicjuje arene po dodaniu/zakonczeniu poprzedniej gry
                new GameStartScheduler(), // planuje rozpoczecie gry gdy arena jest w lobby
                new GameStartListener(), // inicjuje gre po starcie
                new ArenaEndListener()); // pilnuje by arena nie stala pusta i wykonuje czynnosci koncowe

        new MiniGameApi(); // inicjuje zmienne w klasie i statyczną INSTANCE

        for (int i = 0; i < 4; i++) // create 4 arenas.
        {
            this.arenaManager.createArena();
        }
    }

    @Override
    public void stop()
    {
        this.arenaManager.removeArenas();
    }

    /**
     * Konfiguracja minigry uruchamianej na tym serwerze.
     * @return konfiguracja minigry.
     */
    public MiniGame getMiniGame()
    {
        return this.miniGame;
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
        final byte[] bytes = this.msgPack.serialize(IArenaNetEvent.class, event);
        this.subscriber.publish("minigames:arena_event", bytes);
    }

    private void validateConfig(final MiniGame miniGame)
    {
        if (miniGame.getMapVoting().getEnabled() && miniGame.getLobbyMode() != LobbyMode.EXTERNAL)
        {
            throw new ConfigurationException("Map voting can be only enabled when lobby mode is EXTERNAL.");
        }

        if (miniGame.getGameMaps() == null || miniGame.getGameMaps().isEmpty())
        {
            throw new ConfigurationException("You must define at least one map in minigame.yml");
        }
    }
}
