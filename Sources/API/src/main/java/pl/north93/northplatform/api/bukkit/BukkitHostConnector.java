package pl.north93.northplatform.api.bukkit;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import org.spigotmc.SpigotConfig;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.HostConnector;
import pl.north93.northplatform.api.global.component.impl.general.ComponentManagerImpl;
import pl.north93.northplatform.api.global.component.impl.general.WeakClassPool;
import pl.north93.northplatform.api.global.redis.RedisKeys;
import pl.north93.northplatform.api.global.utils.exceptions.ConfigurationException;

public class BukkitHostConnector implements HostConnector
{
    private final Main bukkitPlugin;
    private UUID serverId;

    public BukkitHostConnector(final Main bukkitPlugin)
    {
        this.bukkitPlugin = bukkitPlugin;
    }

    @Override
    public String onPlatformInit(final ApiCore apiCore)
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

        return RedisKeys.SERVER + this.serverId;
    }

    @Override
    public void onPlatformStart(final ApiCore apiCore)
    {
        SpigotConfig.bungee = true; // force enable IP forwarding
    }

    @Override
    public void onPlatformStop(final ApiCore apiCore)
    {
    }

    @Override
    public File getRootDirectory()
    {
        // property handle national characters in path
        final String location = Bukkit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        final String path = URLEncoder.encode(location, StandardCharsets.UTF_8);
        return new File(path).getParentFile();
    }

    @Override
    public File getFile(final String name)
    {
        return new File(this.bukkitPlugin.getDataFolder(), name);
    }

    public final Main getPluginMain()
    {
        return this.bukkitPlugin;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    /**
     * Wywołuje dany event a następnie zwraca jego instancję.
     * @param event event do wywołania.
     * @param <T> typ eventu.
     * @return instancja podana jako argument.
     */
    public <T extends Event> T callEvent(final T event)
    {
        this.bukkitPlugin.getServer().getPluginManager().callEvent(event);
        return event;
    }

    @Override
    public void shutdownHost()
    {
        Bukkit.shutdown();
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable)
    {
        this.bukkitPlugin.getServer().getScheduler().runTaskAsynchronously(this.bukkitPlugin, runnable);
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable, final int ticks)
    {
        this.bukkitPlugin.getServer().getScheduler().runTaskTimerAsynchronously(this.bukkitPlugin, runnable, 0, ticks);
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
        final WeakClassPool classPool = ComponentManagerImpl.instance.getWeakClassPool(this.getClass().getClassLoader());
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins())
        {
            if (plugin == this.bukkitPlugin)
            {
                continue;
            }
            classPool.addClassPath(plugin.getClass().getClassLoader());
        }
    }
}
