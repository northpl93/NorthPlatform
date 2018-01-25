package pl.north93.zgame.daemon.servers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.DioriteUtils;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.network.daemon.config.ServerPatternConfig;
import pl.north93.zgame.daemon.event.ServerCreatingEvent;

public class WorkspaceSetup
{
    @Inject
    private Logger       logger;
    @Inject
    private ApiCore      apiCore;
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
        }
        catch (final Exception e)
        {
            this.logger.log(Level.SEVERE, "Exception while setting up workspace", e);
        }
    }

    private void copyApiFiles(final File workspace) throws IOException
    {
        final File plugins = new File(workspace, "plugins");
        DioriteUtils.createDirectory(plugins);
        final File apiJar = new File(ApiCore.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        FileUtils.copyFileToDirectory(apiJar, plugins);

        final File pluginsApi = new File(plugins, "API");
        DioriteUtils.createDirectory(pluginsApi);
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
