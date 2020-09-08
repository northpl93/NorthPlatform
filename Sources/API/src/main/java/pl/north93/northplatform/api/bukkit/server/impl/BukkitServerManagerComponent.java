package pl.north93.northplatform.api.bukkit.server.impl;

import org.bukkit.Bukkit;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.event.ServerStartedEvent;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.IServerRpc;
import pl.north93.northplatform.api.global.network.server.ServerState;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;

@Slf4j
public class BukkitServerManagerComponent extends Component
{
    @Inject
    private IRpcManager rpcManager;
    @Inject
    private BukkitExecutorImpl bukkitExecutor;
    @Inject
    private BukkitServerManagerImpl bukkitServerManager;

    @Override
    protected void enableComponent()
    {
        if (this.bukkitServerManager.getServer() == null)
        {
            throw new RuntimeException("Not found server data in redis. Ensure that controller is running and serverId is valid.");
        }

        this.bukkitExecutor.asyncTimer(10, this::updatePlayersCount);
        this.bukkitExecutor.sync(() ->
        {
            // this code will execute in first tick after full startup
            this.bukkitServerManager.changeState(ServerState.WORKING);
            this.bukkitServerManager.callEvent(new ServerStartedEvent());
        });

        this.rpcManager.addRpcImplementation(IServerRpc.class, new ServerRpcImpl());
    }

    private void updatePlayersCount()
    {
        final int players = Bukkit.getOnlinePlayers().size();
        if (this.bukkitServerManager.getServer().getPlayersCount() == players)
        {
            return;
        }

        this.bukkitServerManager.updateServerDto(serverDto ->
        {
            serverDto.setPlayersCount(players);
        });
    }

    @Override
    protected void disableComponent()
    {
        this.bukkitServerManager.changeState(ServerState.STOPPING);
    }
}
