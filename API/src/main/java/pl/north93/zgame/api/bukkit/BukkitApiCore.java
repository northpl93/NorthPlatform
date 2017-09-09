package pl.north93.zgame.api.bukkit;

import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.io.File;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import org.spigotmc.SpigotConfig;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import pl.north93.zgame.api.bukkit.packets.PacketsHandler;
import pl.north93.zgame.api.bukkit.windows.WindowManager;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;
import pl.north93.zgame.api.global.exceptions.ConfigurationException;

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

    @Deprecated // mamy nowy menadzer gui
    public WindowManager getWindowManager()
    {
        return this.windowManager;
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

    /**
     * Uruchamia dany task w Bukkit Schedulerze.
     *
     * @param runnable task do uruchomienia.
     */
    public final void run(final Runnable runnable)
    {
        Bukkit.getScheduler().runTask(this.pluginMain, runnable);
    }

    @Override
    public Logger getLogger()
    {
        if (this.pluginMain == null) // can be true in constructor
        {
            return null;
        }
        return this.pluginMain.getLogger();
    }

    @Override
    public String getId()
    {
        return SERVER + this.serverId;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public File getRootDirectory()
    {
        return new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    @Override
    protected void init() throws Exception
    {
        this.registerPluginsPaths();
        try
        {
            this.serverId = this.obtainServerId();
        }
        catch (final ConfigurationException e)
        {
            throw new RuntimeException("Something went wrong ;/", e);
        }
        new PacketsHandler(this.pluginMain);
    }

    @Override
    protected void start() throws Exception
    {
        SpigotConfig.bungee = true; // force enable IP forwarding

        this.registerEvents(this.windowManager);
    }

    @Override
    protected void stop()
    {
    }

    @Override
    public File getFile(final String name)
    {
        return new File(this.pluginMain.getDataFolder(), name);
    }

    /**
     * Rejestruje podane listenery w Bukkicie.
     * @param listeners listenery do zarejestrowania.
     */
    public void registerEvents(final Listener... listeners)
    {
        final PluginManager pluginManager = this.pluginMain.getServer().getPluginManager();
        for (final Listener listener : listeners)
        {
            pluginManager.registerEvents(listener, this.pluginMain);
        }
    }

    /**
     * Wywołuje dany event a następnie zwraca jego instancję.
     * @param event event do wywołania.
     * @param <T> typ eventu.
     * @return instancja podana jako argument.
     */
    public <T extends Event> T callEvent(final T event)
    {
        this.pluginMain.getServer().getPluginManager().callEvent(event);
        return event;
    }

    private UUID obtainServerId() throws ConfigurationException
    {
        final Properties properties = System.getProperties();
        if (properties.containsKey("northplatform.serverid")) // Konfiguracja serwera pobierana jest z Redisa
        {
            return UUID.fromString(properties.getProperty("northplatform.serverid"));
        }
        else
        {
            Bukkit.shutdown();
            throw new ConfigurationException("Invalid startup parameters. Please specify northplatform.serverid");
        }
    }

    /**
     * Rejestruje classloadery wszystkich pluginow w glownym ClassPoolu
     * systemu wstrzykiwania zaleznosci.
     * Potrzebne bo jesli uzyjemy klasy jakiegos innego pluginu to
     * wtedy wstrzykiwanie zaleznosci moze wywalac bledy.
     */
    private void registerPluginsPaths()
    {
        final ClassPool classPool = ComponentManagerImpl.instance.getClassPool(this.getClass().getClassLoader());
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins())
        {
            if (plugin == this.pluginMain)
            {
                continue;
            }
            classPool.appendClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));
        }
    }
}
