package pl.north93.northplatform.api.global;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import org.diorite.commons.io.DioriteFileUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.IComponentManager;
import pl.north93.northplatform.api.global.component.impl.general.ComponentManagerImpl;

@Slf4j
public final class ApiCore
{
    @Getter
    private final boolean isDebug;
    private final IComponentManager componentManager;
    private final HostId hostId;
    private final HostConnector connector;
    private final String hostname;
    private ApiState apiState;
    private String id;

    public ApiCore(final HostId hostId, final HostConnector hostConnector)
    {
        this.hostId = hostId;
        this.connector = hostConnector;

        this.hostname = this.obtainHostName();
        this.isDebug = System.getProperties().containsKey("debug");
        this.componentManager = new ComponentManagerImpl(this);
        this.setApiState(ApiState.CONSTRUCTED);
    }

    public HostId getHostId()
    {
        return this.hostId;
    }

    public HostConnector getHostConnector()
    {
        return this.connector;
    }

    public void startPlatform()
    {
        final long startTime = System.currentTimeMillis();

        Locale.setDefault(new Locale("pl", "PL"));
        log.info("Starting NorthPlatform API");

        try
        {
            this.id = this.connector.onPlatformInit(this);
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
            this.connector.onPlatformStart(this);
        }
        catch (final Exception e)
        {
            log.error("Failed to start NorthPlatform API", e);
            this.connector.shutdownHost();
            return;
        }
        this.setApiState(ApiState.ENABLED);

        final long startDurationMillis = System.currentTimeMillis() - startTime;
        final String formattedDuration = DurationFormatUtils.formatDurationWords(startDurationMillis, true, true);
        log.info("NorthPlatform API started in {}, client id is {}", formattedDuration, this.getId());

        log.debug("If you see this message debug mode is enabled");
    }

    public void stopPlatform()
    {
        try
        {
            this.connector.onPlatformStop(this);
        }
        catch (final Exception e)
        {
            log.error("Failed to stop NorthPlatform API", e);
        }
        this.componentManager.disableAllComponents();
        this.setApiState(ApiState.DISABLED);
        log.info("NorthPlatform API stopped.");
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

    public ApiState getApiState()
    {
        return this.apiState;
    }

    public String getId()
    {
        return this.id;
    }

    /**
     * @return główny katalog serwera/bungeecorda.
     */
    public File getRootDirectory()
    {
        return this.connector.getRootDirectory();
    }

    /**
     * @return file inside plugin's configuration directory
     */
    public File getFile(final String name)
    {
        return this.connector.getFile(name);
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
}
