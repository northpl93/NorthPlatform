package pl.north93.northplatform.api.bukkit;


import java.io.File;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import org.spigotmc.SpigotConfig;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.Platform;
import pl.north93.northplatform.api.global.component.impl.general.ComponentManagerImpl;
import pl.north93.northplatform.api.global.redis.RedisKeys;
import pl.north93.northplatform.api.global.utils.exceptions.ConfigurationException;
import pl.north93.northplatform.api.global.utils.lang.SneakyThrow;

public class BukkitApiCore extends ApiCore
{
    private final Main pluginMain;
    private       UUID serverId;

    public BukkitApiCore(final Main plugin)
    {
        super(Platform.BUKKIT, new BukkitPlatformConnector(plugin));
        this.pluginMain = plugin;
    }

    public final Main getPluginMain()
    {
        return this.pluginMain;
    }

    public final org.bukkit.Server getBukkit()
    {
        return this.pluginMain.getServer();
    }

    @Override
    public String getId()
    {
        return RedisKeys.SERVER + this.serverId;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public File getRootDirectory()
    {
        // property handle national characters in path
        String location = Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String path = SneakyThrow.sneaky(() -> URLEncoder.encode(location, "UTF-8"));
        return new File(path).getParentFile();
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
    }

    @Override
    protected void start() throws Exception
    {
        SpigotConfig.bungee = true; // force enable IP forwarding
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
