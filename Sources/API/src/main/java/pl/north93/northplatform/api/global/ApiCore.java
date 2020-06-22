package pl.north93.northplatform.api.global;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import org.diorite.commons.io.DioriteFileUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.agent.InstrumentationClient;
import pl.north93.northplatform.api.global.component.IComponentManager;
import pl.north93.northplatform.api.global.component.impl.general.ComponentManagerImpl;

@Slf4j
public abstract class ApiCore
{
    @Getter
    private final boolean isDebug;
    private final InstrumentationClient instrumentationClient;
    private final IComponentManager componentManager;
    private final Platform platform;
    private final PlatformConnector connector;
    private final String hostname;
    private ApiState apiState;

    public ApiCore(final Platform platform, final PlatformConnector platformConnector)
    {
        this.platform = platform;
        this.connector = platformConnector;

        this.hostname = this.obtainHostName();
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
        log.info("Starting NorthPlatform API");

        try
        {
            this.init();
            this.setApiState(ApiState.INITIALISED);
        }
        catch (final Exception e)
        {
            log.error("Failed to initialise NorthPlatform API", e);
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
            log.error("Failed to start NorthPlatform API", e);
            this.getPlatformConnector().stop();
            return;
        }
        this.setApiState(ApiState.ENABLED);
        log.info("Client id is {}", this.getId());
        log.debug("If you see this message debug mode is enabled");
    }

    public final void stopCore()
    {
        try
        {
            this.stop();
        }
        catch (final Exception e)
        {
            log.error("Failed to stop NorthPlatform API", e);
        }
        this.componentManager.disableAllComponents();
        this.setApiState(ApiState.DISABLED);
        log.info("NorthPlatform API stopped.");
    }

    public InstrumentationClient getInstrumentationClient()
    {
        return this.instrumentationClient;
    }

    public IComponentManager getComponentManager()
    {
        return this.componentManager;
    }

    /**
     * Returns machine's network name.
     *
     * @return host name
     */
    public String getHostName()
    {
        return this.hostname;
    }

    private String obtainHostName()
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
        log.debug("NorthPlatform API forced into {} state.", newState);
    }

    public final ApiState getApiState()
    {
        return this.apiState;
    }

    protected final Logger getApiLogger()
    {
        return log;
    }

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
