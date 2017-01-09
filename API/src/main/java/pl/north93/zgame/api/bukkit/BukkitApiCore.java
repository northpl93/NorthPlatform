package pl.north93.zgame.api.bukkit;

import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.io.File;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import org.spigotmc.SpigotConfig;

import pl.north93.zgame.api.bukkit.listeners.ChatListener;
import pl.north93.zgame.api.bukkit.listeners.JoinLeftListener;
import pl.north93.zgame.api.bukkit.windows.WindowManager;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.exceptions.ConfigurationException;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.redis.observable.Value;

public class BukkitApiCore extends ApiCore
{
    private final Main          pluginMain;
    private final WindowManager windowManager;
    private       UUID          serverId;

    public BukkitApiCore(final Main plugin)
    {
        super(Platform.BUKKIT, new BukkitPlatformConnector(plugin));
        this.pluginMain = plugin;
        this.windowManager = new WindowManager();
    }

    public final Main getPluginMain()
    {
        return this.pluginMain;
    }

    public WindowManager getWindowManager()
    {
        return this.windowManager;
    }

    public final Value<Server> getServer()
    {
        return this.getNetworkManager().getServer(this.serverId).setIfUnavailable(this::generateServer);
    }

    public final org.bukkit.Server getBukkit()
    {
        return this.pluginMain.getServer();
    }

    /**
     * Synchronizuje podany runnable do wątku serwera przy użyciu Schedulera.
     *
     * @param runnable do zsynchronizowania.
     */
    public final void sync(final Runnable runnable)
    {
        this.pluginMain.getServer().getScheduler().runTask(this.pluginMain, runnable);
    }

    /**
     * Wykonuje kod w pierwszym argumencie asynchronicznie,
     * wynik przekazuje do drugiego i wykonuje go synchronicznie.
     * Jeś zostanie zwrócony null częsc synchroniczna sie nie wykona.
     *
     * @param async kod asynchroniczny.
     * @param synced kod zsynchronizowany do serwerra.
     * @param <T> wartość przekazywana z kodu asynchronicznego do synchronicznego.
     */
    public final <T> void sync(final Supplier<T> async, final Consumer<T> synced)
    {
        this.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final T t = async.get();
            if (t != null)
            {
                this.sync(() -> synced.accept(t));
            }
        });
    }

    @Override
    public Logger getLogger()
    {
        return this.pluginMain.getLogger();
    }

    @Override
    public String getId()
    {
        return SERVER + this.serverId;
    }

    @Override
    protected void init() throws Exception
    {
        try
        {
            this.serverId = this.getServerId();
        }
        catch (final ConfigurationException e)
        {
            throw new RuntimeException("Something went wrong ;/", e);
        }
    }

    @Override
    protected void start() throws Exception
    {
        SpigotConfig.bungee = true; // force enable IP forwarding

        this.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final ServerImpl impl = (ServerImpl) this.getServer().get();
            impl.setServerState(ServerState.WORKING);
            this.getServer().set(impl);
        }); // on bukkit it will be invoked after server start

        this.registerEvents(new JoinLeftListener(), new ChatListener(), this.windowManager);
    }

    @Override
    protected void stop()
    {
        if (! this.getServer().get().isLaunchedViaDaemon())
        {
            this.getServer().delete(); // remove key from redis is that server is not managed by daemon
        }
    }

    @Override
    public File getFile(final String name)
    {
        return new File(this.pluginMain.getDataFolder(), name);
    }

    private void registerEvents(final Listener... listeners)
    {
        for (final Listener listener : listeners)
        {
            this.pluginMain.getServer().getPluginManager().registerEvents(listener, this.pluginMain);
        }
    }

    private UUID getServerId() throws ConfigurationException
    {
        final Properties properties = System.getProperties();
        if (properties.containsKey("northplatform.serverid")) // Konfiguracja serwera pobierana jest z Redisa
        {
            this.debug("Server is identified by northplatform.serverid");
            return UUID.fromString(properties.getProperty("northplatform.serverid"));
        }
        else if (properties.containsKey("northplatform.servertype")) // Konfiguracja ręczna - serwer sam zgłasza się do Redisa
        {
            this.debug("Server identity is generated (northplatform.servertype)");
            return UUID.randomUUID();
        }
        else
        {
            throw new ConfigurationException("Invalid startup parameters. Please specify northplatform.serverid or northplatform.servertype");
        }
    }

    private Server generateServer()
    {
        final Properties properties = System.getProperties();
        return new ServerImpl(this.serverId, false, ServerType.valueOf(properties.getProperty("northplatform.servertype")), ServerState.STARTING, JoiningPolicy.EVERYONE, Bukkit.getIp(), Bukkit.getPort());
    }
}
