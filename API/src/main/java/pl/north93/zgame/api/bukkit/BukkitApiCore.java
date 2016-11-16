package pl.north93.zgame.api.bukkit;

import java.io.File;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.event.Listener;

import org.spigotmc.SpigotConfig;

import pl.north93.zgame.api.bukkit.cmd.KickCmd;
import pl.north93.zgame.api.bukkit.cmd.MsgCmd;
import pl.north93.zgame.api.bukkit.cmd.NetworkCmd;
import pl.north93.zgame.api.bukkit.cmd.NetworkControllerPing;
import pl.north93.zgame.api.bukkit.cmd.Performance;
import pl.north93.zgame.api.bukkit.cmd.PlayerInfoCmd;
import pl.north93.zgame.api.bukkit.cmd.WtfServer;
import pl.north93.zgame.api.bukkit.listeners.ChatListener;
import pl.north93.zgame.api.bukkit.listeners.JoinLeftListener;
import pl.north93.zgame.api.bukkit.windows.WindowManager;
import pl.north93.zgame.api.global.API;
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

    public Main getPluginMain()
    {
        return this.pluginMain;
    }

    public WindowManager getWindowManager()
    {
        return this.windowManager;
    }

    public Server getServer()
    {
        return this.thisServer;
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
        API.getPlatformConnector().runTaskAsynchronously(() -> this.thisServer.updateServerState(ServerState.WORKING)); // on bukkit it will be invoked after server start
        this.identifyServer();
        this.registerEvents(new JoinLeftListener(), new ChatListener(), this.windowManager);

        this.pluginMain.getCommand("playerinfo").setExecutor(new PlayerInfoCmd());
        this.pluginMain.getCommand("msg").setExecutor(new MsgCmd());
        this.pluginMain.getCommand("kick").setExecutor(new KickCmd());
        this.pluginMain.getCommand("network").setExecutor(new NetworkCmd());
        this.pluginMain.getCommand("wtfserver").setExecutor(new WtfServer());
        this.pluginMain.getCommand("performance").setExecutor(new Performance());
        this.pluginMain.getCommand("networkcontrollerping").setExecutor(new NetworkControllerPing());
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
    protected File getFile(final String name)
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
