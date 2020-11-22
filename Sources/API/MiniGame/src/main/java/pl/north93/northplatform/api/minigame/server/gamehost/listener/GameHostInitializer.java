package pl.north93.northplatform.api.minigame.server.gamehost.listener;

import org.bukkit.event.EventHandler;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostRpcImpl;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.server.DestroyGameHostServerEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.server.InitializeGameHostServerEvent;
import pl.north93.northplatform.api.minigame.shared.api.IGameHostRpc;

public class GameHostInitializer implements AutoListener
{
    @Inject
    private IRpcManager rpcManager;
    @Inject
    private IBukkitExecutor bukkitExecutor;
    @Inject
    private GameHostManager gameHostManager;
    @Inject
    private LocalArenaManager localArenaManager;

    @EventHandler
    public void handleInitialization(final InitializeGameHostServerEvent event)
    {
        this.rpcManager.addRpcImplementation(IGameHostRpc.class, new GameHostRpcImpl(this.localArenaManager));

        this.bukkitExecutor.sync(this::createArenas);
    }

    private void createArenas()
    {
        for (int i = 0; i < this.gameHostManager.getMiniGameConfig().getArenas(); i++) // create arenas.
        {
            this.localArenaManager.createArena();
        }
    }

    @EventHandler
    public void handleDestroy(final DestroyGameHostServerEvent event)
    {
        this.localArenaManager.removeArenas();
    }
}
