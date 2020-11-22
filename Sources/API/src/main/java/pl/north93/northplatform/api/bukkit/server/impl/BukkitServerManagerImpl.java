package pl.north93.northplatform.api.bukkit.server.impl;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.BukkitHostConnector;
import pl.north93.northplatform.api.bukkit.Main;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.server.event.ShutdownCancelledEvent;
import pl.north93.northplatform.api.bukkit.server.event.ShutdownScheduledEvent;
import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.ServerState;
import pl.north93.northplatform.api.global.redis.observable.Value;

@Slf4j
class BukkitServerManagerImpl implements IBukkitServerManager
{
    private static final int TIME_TO_NEXT_TRY = 30 * 20; // 30 seconds
    @Inject
    private IServersManager serversManager;
    @Inject
    private BukkitHostConnector hostConnector;
    // - - - - - - -
    private final SimpleCountdown countdown;
    private final Value<ServerDto> serverValue;

    @Bean
    private BukkitServerManagerImpl()
    {
        this.countdown = new SimpleCountdown(TIME_TO_NEXT_TRY).endCallback(this::tryShutdown);
        this.serverValue = this.serversManager.unsafe().getServerDto(this.hostConnector.getServerId());
    }

    @Override
    public UUID getServerId()
    {
        return this.hostConnector.getServerId();
    }

    @Override
    public Server getServer()
    {
        // can return null when network isn't properly configured or initialised by controller
        return this.serverValue.get();
    }

    @Bean
    @Override
    public JavaPlugin getPlugin()
    {
        return this.hostConnector.getPluginMain();
    }

    public void updateServerDto(final Consumer<ServerDto> updater)
    {
        this.serverValue.update(updater);
    }

    @Override
    public void registerEvents(final Listener... listeners)
    {
        final Main pluginMain = this.hostConnector.getPluginMain();

        final PluginManager pluginManager = pluginMain.getServer().getPluginManager();
        for (final Listener listener : listeners)
        {
            pluginManager.registerEvents(listener, pluginMain);
        }
    }

    @Override
    public <T extends Event> T callEvent(final T event)
    {
        return this.hostConnector.callEvent(event);
    }

    @Override
    public File getServerDirectory()
    {
        return this.hostConnector.getRootDirectory();
    }

    @Override
    public void changeState(final ServerState newState)
    {
        log.info("Server {} forced into {} state by BukkitServerManagerImpl#changeState()", this.getServerId(), newState);
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

        log.info("Scheduling server shutdown...");
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

        log.info("Server shutdown cancelled...");
        this.serverValue.update(server ->
        {
            server.setShutdownScheduled(false);
        });

        this.countdown.stop();
        this.callEvent(new ShutdownCancelledEvent());
    }

    private void tryShutdown()
    {
        final ShutdownScheduledEvent event = this.callEvent(new ShutdownScheduledEvent());
        if (! event.isCancelled())
        {
            log.info("Shutting down server because shutdown was scheduled and not deferred");
            Bukkit.shutdown();
            return;
        }

        this.countdown.reset(TIME_TO_NEXT_TRY);
        this.countdown.start();
    }

    @Override
    public FixedMetadataValue createFixedMetadataValue(final Object value)
    {
        return new FixedMetadataValue(this.hostConnector.getPluginMain(), value);
    }
}
