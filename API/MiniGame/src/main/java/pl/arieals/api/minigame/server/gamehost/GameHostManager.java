package pl.arieals.api.minigame.server.gamehost;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectNewInstance;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;

public class GameHostManager implements IServerManager
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager       rpcManager;
    @InjectNewInstance
    private LocalArenaManager arenaManager;

    @Override
    public void start()
    {
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

    public LocalArenaManager getArenaManager()
    {
        return this.arenaManager;
    }
}
