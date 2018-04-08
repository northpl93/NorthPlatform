package pl.north93.zgame.api.global;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import org.diorite.commons.io.DioriteFileUtils;

import pl.north93.zgame.api.global.agent.InstrumentationClient;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.annotations.ProvidesComponent;
import pl.north93.zgame.api.global.component.impl.general.ComponentManagerImpl;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.permissions.PermissionsManager;

public abstract class ApiCore
{
    private final boolean            isDebug;
    private final InstrumentationClient       instrumentationClient;
    private final IComponentManager  componentManager;
    private final Platform           platform;
    private final PlatformConnector  connector;
    private       ApiState           apiState;

    public ApiCore(final Platform platform, final PlatformConnector platformConnector)
    {
        this.platform = platform;
        this.connector = platformConnector;
        this.isDebug = System.getProperties().containsKey("debug");
        this.instrumentationClient = new InstrumentationClient();
        this.componentManager = new ComponentManagerImpl(this);
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
        final File components = this.getFile("components");
        DioriteFileUtils.createDirectory(components);
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

    public InstrumentationClient getInstrumentationClient()
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
