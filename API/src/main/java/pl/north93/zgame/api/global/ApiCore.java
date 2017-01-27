package pl.north93.zgame.api.global;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ea.agentloader.AgentLoader;

import org.apache.commons.io.FileUtils;

import org.diorite.utils.DioriteUtils;

import pl.north93.zgame.api.global.agent.client.IAgentClient;
import pl.north93.zgame.api.global.agent.client.LocalAgentClient;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.annotations.ProvidesComponent;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;
import pl.north93.zgame.api.global.data.UsernameCache;
import pl.north93.zgame.api.global.exceptions.SingletonException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.impl.RpcManagerImpl;

public abstract class ApiCore
{
    private final IAgentClient       instrumentationClient;
    private final IComponentManager  componentManager;
    private final Platform           platform;
    private final PlatformConnector  connector;
    private final TemplateManager    messagePackTemplates;
    private final IRpcManager        rpcManager;

    public ApiCore(final Platform platform, final PlatformConnector platformConnector)
    {
        this.platform = platform;
        this.connector = platformConnector;
        this.instrumentationClient = new LocalAgentClient();
        this.componentManager = new ComponentManagerImpl(this);
        this.messagePackTemplates = new TemplateManagerImpl();
        this.rpcManager = new RpcManagerImpl();
        try
        {
            API.setApiCore(this);
        }
        catch (final SingletonException e)
        {
            throw new RuntimeException("You can't create more than one ApiCore class instances.", e);
        }
    }

    public final Platform getPlatform()
    {
        return this.platform;
    }

    public PlatformConnector getPlatformConnector()
    {
        return this.connector;
    }

    public final void startCore()
    {
        Locale.setDefault(new Locale("pl", "PL"));
        this.getLogger().info("Starting North API Core.");
        this.setupInstrumentation();

        try
        {
            this.init();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return;
        }

        this.componentManager.doComponentScan(this.getClass().getClassLoader()); // scan for builtin API components
        this.componentManager.injectComponents(this.messagePackTemplates, this.rpcManager); // inject base API components
        final File components = this.getFile("components");
        DioriteUtils.createDirectory(components);
        this.componentManager.doComponentScan(components); // Scan components directory and enable components
        this.componentManager.enableAllComponents(); // enable all components
        this.componentManager.setAutoEnable(true); // auto enable all newly discovered components

        try
        {
            this.start();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            this.getPlatformConnector().stop();
        }
        this.getLogger().info("Client id is " + this.getId());
    }

    private void setupInstrumentation()
    {
        final File extractedAgent = this.getFile("NorthPlatformInstrumentation.jar");
        if (!extractedAgent.exists())
        {
            URL inputUrl = this.getClass().getResource("/InstrumentationAgent.jar");
            try
            {
                FileUtils.copyURLToFile(inputUrl, extractedAgent);
            }
            catch (final IOException e)
            {
                e.printStackTrace();
                return;
            }
        }
        AgentLoader.loadAgent(extractedAgent.toString(), "");
        this.instrumentationClient.connect();
    }

    public final void stopCore()
    {
        try
        {
            this.stop();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        this.componentManager.disableAllComponents();
        this.getLogger().info("North API Core stopped.");
    }

    @ProvidesComponent
    public INetworkManager getNetworkManager()
    {
        return this.componentManager.getComponent("API.MinecraftNetwork.NetworkManager");
    }

    @ProvidesComponent
    public PermissionsManager getPermissionsManager()
    {
        return this.componentManager.getComponent("API.MinecraftNetwork.PermissionsManager");
    }

    @ProvidesComponent
    public UsernameCache getUsernameCache()
    {
        return this.componentManager.getComponent("API.MinecraftNetwork.UsernameCache");
    }

    @ProvidesComponent
    @Deprecated
    public TemplateManager getMessagePackTemplates()
    {
        return this.messagePackTemplates;
    }

    @ProvidesComponent
    public IRpcManager getRpcManager()
    {
        return this.rpcManager;
    }

    public IAgentClient getInstrumentationClient()
    {
        return this.instrumentationClient;
    }

    public IComponentManager getComponentManager()
    {
        return this.componentManager;
    }

    public void debug(final Object object)
    {
        this.getLogger().log(Level.INFO, "[DEBUG] " + object.toString());
    }

    /**
     * Returns machine's network name.
     *
     * @return host name
     */
    public String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (final UnknownHostException e)
        {
            return "<unknown:UnknownHostException>";
        }
    }

    public abstract Logger getLogger();

    public abstract String getId();

    /**
     * @return file inside plugin's configuration directory
     */
    public abstract File getFile(final String name);

    protected abstract void init() throws Exception; // before components. Good place to provide client ID.

    protected abstract void start() throws Exception; // after components

    protected abstract void stop() throws Exception; // before components
}
