package pl.north93.zgame.api.bukkit.server.impl;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.bukkit.server.IBukkitServerManager;
import pl.north93.zgame.api.bukkit.server.event.ServerStartedEvent;
import pl.north93.zgame.api.bukkit.server.event.ShutdownCancelledEvent;
import pl.north93.zgame.api.bukkit.server.event.ShutdownScheduledEvent;
import pl.north93.zgame.api.bukkit.utils.SimpleCountdown;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.network.server.IServerRpc;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;

public class BukkitServerManagerImpl extends Component implements IBukkitServerManager, IBukkitExecutor
{
    private static final int TIME_TO_NEXT_TRY = 30 * 20; // 30 sekund
    private final Logger logger = LoggerFactory.getLogger(BukkitServerManagerImpl.class);
    @Inject
    private BukkitApiCore    apiCore;
    @Inject
    private INetworkManager  networkManager;
    @Inject
    private IRpcManager      rpcManager;
    // - - - - - - -
    private Value<ServerDto> serverValue;
    private SimpleCountdown  countdown;

    @Override
    protected void enableComponent()
    {
        this.asyncTimer(10, this::updatePlayersCount);
        this.sync(() ->
        {
            // po pelnym uruchomieniu serwera zmianiamy stan na wlaczony i wykonujemy event
            this.changeState(ServerState.WORKING);
            this.apiCore.callEvent(new ServerStartedEvent());
        });

        this.rpcManager.addRpcImplementation(IServerRpc.class, new ServerRpcImpl());
        this.countdown = new SimpleCountdown(TIME_TO_NEXT_TRY).endCallback(this::tryShutdown);

        this.serverValue = this.networkManager.getServers().unsafe().getServerDto(this.apiCore.getServerId());

        if (! this.serverValue.isAvailable())
        {
            throw new RuntimeException("Not found server data in redis. Ensure that controller is running and serverId is valid.");
        }
    }

    private void updatePlayersCount()
    {
        final int players = Bukkit.getOnlinePlayers().size();
        if (this.getServer().getPlayersCount() != players)
        {
            this.serverValue.update((Consumer<ServerDto>) serverDto -> serverDto.setPlayersCount(players));
        }
    }

    @Override
    protected void disableComponent()
    {
        this.changeState(ServerState.STOPPING);

        final UUID serverId = this.apiCore.getServerId();
        this.logger.info("Server {} forced into STOPPING state by BukkitServerManagerImpl", serverId);
    }

    @Override
    public Server getServer()
    {
        return this.serverValue.get();
    }

    @Override
    public void changeState(final ServerState newState)
    {
        this.serverValue.update(server ->
        {
            server.setServerState(newState);
        });
    }

    @Override
    public boolean isWorking()
    {
        return MinecraftServer.getServer().isRunning();
    }

    @Override
    public boolean isShutdownScheduled()
    {
        return this.getServer().isShutdownScheduled();
    }

    @Override
    public void scheduleShutdown()
    {
        Preconditions.checkState(! this.isShutdownScheduled(), "Shutdown already scheduled");

        this.logger.info("Scheduling server shutdown...");
        this.serverValue.update(server ->
        {
            server.setShutdownScheduled(true);
        });

        this.tryShutdown();
    }

    @Override
    public void cancelShutdown()
    {
        Preconditions.checkState(this.isShutdownScheduled(), "Shutdown isn't scheduled");
        Preconditions.checkState(this.isWorking(), "Server is already stopping");

        this.logger.info("Server shutdown cancelled...");
        this.serverValue.update(server ->
        {
            server.setShutdownScheduled(false);
        });

        this.countdown.stop();
        this.apiCore.callEvent(new ShutdownCancelledEvent());
    }

    private void tryShutdown()
    {
        final ShutdownScheduledEvent event = this.apiCore.callEvent(new ShutdownScheduledEvent());
        if (! event.isCancelled())
        {
            this.logger.info("Shutting down server because shutdown was scheduled and not deferred");
            Bukkit.shutdown();
            return;
        }

        this.countdown.reset(TIME_TO_NEXT_TRY);
        this.countdown.start();
    }

    @Override
    public void sync(final Runnable runnable)
    {
        Bukkit.getScheduler().runTask(this.apiCore.getPluginMain(), runnable);
    }

    @Override
    public void async(final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskAsynchronously(this.apiCore.getPluginMain(), runnable);
    }

    @Override
    public void syncLater(final int ticks, final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskLater(this.apiCore.getPluginMain(), runnable, ticks);
    }

    @Override
    public void asyncLater(final int ticks, final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.apiCore.getPluginMain(), runnable, ticks);
    }

    @Override
    public <T> void mixed(final Supplier<T> async, final Consumer<T> synced)
    {
        this.async(() ->
        {
            final T t = async.get();
            if (t != null)
            {
                this.sync(() -> synced.accept(t));
            }
        });
    }

    @Override
    public void syncTimer(final int every, final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskTimer(this.apiCore.getPluginMain(), runnable, every, every);
    }

    @Override
    public void asyncTimer(final int every, final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.apiCore.getPluginMain(), runnable, every, every);
    }

    @Override
    public void inMainThread(final Runnable runnable)
    {
        if (MinecraftServer.getServer().isMainThread())
        {
            runnable.run();
        }
        else
        {
            this.sync(runnable);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverValue", this.serverValue).append("countdown", this.countdown).toString();
    }
}
