package pl.arieals.api.minigame.server.gamehost;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.arieals.api.minigame.server.gamehost.listener.PlayerListener;
import pl.arieals.api.minigame.server.gamehost.listener.WorldListener;
import pl.arieals.api.minigame.server.gamehost.lobby.ExternalLobby;
import pl.arieals.api.minigame.server.gamehost.lobby.ILobbyManager;
import pl.arieals.api.minigame.server.gamehost.lobby.IntegratedLobby;
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
    private ILobbyManager     lobbyManager;
    private MiniGame          miniGame;

    @Override
    public void start()
    {
        this.miniGame = ConfigUtils.loadConfigFile(MiniGame.class, this.apiCore.getFile("minigame.yml"));
        this.rpcManager.addRpcImplementation(IGameHostRpc.class, new GameHostRpcImpl(this));
        this.lobbyManager = (this.miniGame.getLobbyMode() == LobbyMode.EXTERNAL) ? new ExternalLobby() : new IntegratedLobby();

        this.apiCore.registerEvents(new PlayerListener(), new WorldListener());

        for (int i = 0; i < 4; i++) // create 4 arenas.
        {
            this.arenaManager.createArena();
        }
    }

    @Override
    public void stop()
    {
    }

    /**
     * Konfiguracja minigry uruchamianej na tym serwerze.
     * @return konfiguracja minigry.
     */
    public MiniGame getMiniGame()
    {
        return this.miniGame;
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

    public void publishArenaEvent(final IArenaNetEvent event)
    {
        final byte[] bytes = this.msgPack.serialize(IArenaNetEvent.class, event);
        this.subscriber.publish("minigames:arena_event", bytes);
    }
}
