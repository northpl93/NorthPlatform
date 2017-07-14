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
import org.apache.commons.lang3.StringUtils;

import org.diorite.utils.DioriteUtils;

import pl.north93.zgame.api.global.agent.client.IAgentClient;
import pl.north93.zgame.api.global.agent.client.LocalAgentClient;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.annotations.ProvidesComponent;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;
import pl.north93.zgame.api.global.data.UsernameCache;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.impl.RpcManagerImpl;

public abstract class ApiCore
{
    private final boolean            isDebug;
    private final IAgentClient       instrumentationClient;
    private final IComponentManager  componentManager;
    private final Platform           platform;
    private final PlatformConnector  connector;
    //private final UpdateManager      updateManager;
    private final TemplateManager    messagePackTemplates;
    private final IRpcManager        rpcManager;
    private       ApiState           apiState;

    public ApiCore(final Platform platform, final PlatformConnector platformConnector)
    {
        this.platform = platform;
        this.connector = platformConnector;
        this.isDebug = System.getProperties().containsKey("debug");
        this.instrumentationClient = new LocalAgentClient();
        this.componentManager = new ComponentManagerImpl(this);
        //this.updateManager = new UpdateManager();
        this.messagePackTemplates = new TemplateManagerImpl();
        this.rpcManager = new RpcManagerImpl();
        API.setApiCore(this);
        this.setApiState(ApiState.CONSTRUCTED);
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
            this.setApiState(ApiState.INITIALISED);
        }
        catch (final Exception e)
        {
            this.getLogger().log(Level.SEVERE, "Failed to initialise North API", e);
            return;
        }

        ((ComponentManagerImpl) this.componentManager).initDefaultBeans();
        this.componentManager.doComponentScan(this.getClass().getClassLoader()); // scan for builtin API components
        this.componentManager.injectComponents(/*this.updateManager,*/ this.messagePackTemplates, this.rpcManager); // inject base API components
        final File components = this.getFile("components");
        DioriteUtils.createDirectory(components);
        this.componentManager.doComponentScan(components); // Scan components directory
        final String extraDirectory = System.getProperty("northplatform.components");
        if (! StringUtils.isEmpty(extraDirectory))
        {
            this.componentManager.doComponentScan(new File(extraDirectory));
        }
        this.componentManager.setAutoEnable(true); // components may do own componentScan. So enable auto enable after discovery.
        this.componentManager.enableAllComponents(); // enable all components

        try
        {
            this.start();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            this.getPlatformConnector().stop();
            return;
        }
        this.setApiState(ApiState.ENABLED);
        this.getLogger().info("Client id is " + this.getId());
        this.debug("Debug mode is enabled");
    }

    protected void setupInstrumentation()
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
        this.setApiState(ApiState.DISABLED);
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
        if (! this.isDebug)
        {
            return;
        }

        final Logger logger = this.getLogger();
        if (logger == null)
        {
            System.out.println("[DEBUG] " + object);
        }
        else
        {
            logger.log(Level.INFO, "[DEBUG] " + object.toString());
        }
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

    private void setApiState(final ApiState newState)
    {
        this.apiState = newState;
        this.debug("Api forced into " + newState + " state.");
    }

    public final ApiState getApiState()
    {
        return this.apiState;
    }

    public abstract Logger getLogger();

    public abstract String getId();

    /**
     * @return główny katalog serwera/bungeecorda.
     */
    public abstract File getRootDirectory();

    /**
     * @return file inside plugin's configuration directory
     */
    public abstract File getFile(final String name);

    protected abstract void init() throws Exception; // before components. Good place to provide client ID.

    protected abstract void start() throws Exception; // after components

    protected abstract void stop() throws Exception; // before components
}
