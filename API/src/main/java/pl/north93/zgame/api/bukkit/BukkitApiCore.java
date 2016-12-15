package pl.north93.zgame.api.bukkit;

import java.io.File;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

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

public class BukkitApiCore extends ApiCore
{
    private final Main          pluginMain;
    private final WindowManager windowManager;
    private       ServerImpl    thisServer;

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

    public final Server getServer()
    {
        return this.thisServer;
    }

    public final org.bukkit.Server getBukkit()
    {
        return this.pluginMain.getServer();
    }

    @Override
    public Logger getLogger()
    {
        return this.pluginMain.getLogger();
    }

    @Override
    public String getId()
    {
        return this.thisServer.getRedisKey();
    }

    @Override
    protected void start() throws Exception
    {
        SpigotConfig.bungee = true; // force enable IP forwarding
        this.getPlatformConnector().runTaskAsynchronously(() -> this.thisServer.updateServerState(ServerState.WORKING)); // on bukkit it will be invoked after server start
        this.identifyServer();
        this.registerEvents(new JoinLeftListener(), new ChatListener(), this.windowManager);
    }

    @Override
    protected void stop()
    {
        if (! this.thisServer.isLaunchedViaDaemon())
        {
            this.thisServer.delete(); // remove key from redis is that server is not managed by daemon
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

    private void identifyServer() throws Exception
    {
        final Properties properties = System.getProperties();
        if (properties.containsKey("northplatform.serverid")) // Konfiguracja serwera pobierana jest z Redisa
        {
            this.debug("Server is identified by northplatform.serverid");
            this.thisServer = (ServerImpl) this.getNetworkManager().getServer(UUID.fromString(properties.get("northplatform.serverid").toString()));
        }
        else if (properties.containsKey("northplatform.servertype")) // Konfiguracja ręczna - serwer sam zgłasza się do Redisa
        {
            this.debug("Server identity is generated (northplatform.servertype)");
            this.thisServer = new ServerImpl(UUID.randomUUID(), false, ServerType.valueOf(properties.get("northplatform.servertype").toString()), ServerState.STARTING, JoiningPolicy.EVERYONE);
            this.thisServer.sendUpdate();
        }
        else
        {
            throw new ConfigurationException("Invalid startup parameters. Please specify northplatform.serverid or northplatform.servertype");
        }
    }
}
