package pl.north93.northplatform.daemon.servers.setup;

import java.io.File;
import java.io.IOException;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.io.DioriteFileUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.network.daemon.config.ServerPatternConfig;
import pl.north93.northplatform.api.global.utils.JavaArguments;
import pl.north93.northplatform.daemon.event.ServerCreatingEvent;
import pl.north93.northplatform.daemon.servers.FilesManager;

@Slf4j
public class WorkspaceSetup
{
    @Inject
    private ApiCore apiCore;
    @Inject
    private FilesManager filesManager;

    @Bean
    private WorkspaceSetup(final @Named("daemon") EventBus eventBus)
    {
        eventBus.register(this);
    }

    @Subscribe
    public void setupWorkspace(final ServerCreatingEvent event)
    {
        try
        {
            this.copyComponents(event.getWorkspace(), event.getPattern());
            this.copyApiFiles(event.getWorkspace());
            this.setupLog4j(event);
        }
        catch (final Exception e)
        {
            log.error("Exception while setting up workspace", e);
        }
    }

    private void copyApiFiles(final File workspace) throws IOException
    {
        final File plugins = new File(workspace, "plugins");
        DioriteFileUtils.createDirectory(plugins);
        final File apiJar = new File(ApiCore.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        FileUtils.copyFileToDirectory(apiJar, plugins);

        final File pluginsApi = new File(plugins, "API");
        DioriteFileUtils.createDirectory(pluginsApi);
        final File connectionXml = this.apiCore.getFile("connection.xml");
        FileUtils.copyFileToDirectory(connectionXml, pluginsApi);
    }

    private void copyComponents(final File workspace, final ServerPatternConfig pattern) throws IOException
    {
        for (final String component : pattern.getComponents())
        {
            FileUtils.copyDirectory(this.filesManager.getPattern(component), workspace);
        }
    }

    private void setupLog4j(final ServerCreatingEvent event)
    {
        final File config = new File(event.getWorkspace(), "log4j2.xml");
        if (! config.isFile())
        {
            return;
        }

        final JavaArguments java = event.getArguments();

        java.addEnvVar("log4j.configurationFile", config.getAbsolutePath());
        java.addEnvVar("logstash-gelf.skipHostnameResolution", "true");

        final String serverId = event.getServerDtoValue().get().getUuid().toString();
        java.addEnvVar("logstash-gelf.hostname", serverId);
        java.addEnvVar("logstash-gelf.fqdn.hostname", serverId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
