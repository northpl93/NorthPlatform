package pl.arieals.api.minigame.server.gamehost;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.MiniGame;
import pl.arieals.api.minigame.shared.api.arena.netevent.IArenaNetEvent;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectNewInstance;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;

public class GameHostManager implements IServerManager
{
    @InjectComponent("")
    private TemplateManager   msgPack;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager       rpcManager;
    @InjectComponent("")
    private RedisSubscriber   subscriber;
    @InjectNewInstance
    private LocalArenaManager arenaManager;
    private MiniGame          miniGame;

    @Override
    public void start()
    {
        // todo load minigame config
        this.rpcManager.addRpcImplementation(IGameHostRpc.class, new GameHostRpcImpl(this));

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
