package pl.north93.northplatform.controller.servers;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.controller.servers.groups.LocalGroupsManager;
import pl.north93.northplatform.controller.configserver.IConfigServer;
import pl.north93.northplatform.controller.configserver.source.XmlConfigSource;
import pl.north93.northplatform.api.global.network.daemon.config.AutoScalingConfig;

public class NetworkServersManager extends Component
{
    @Inject
    private IConfigServer      configServer;
    @Inject
    private LocalGroupsManager localGroupsManager;

    @Override
    protected void enableComponent()
    {
        final File scalerConfig = this.getApiCore().getFile("autoscaler.xml");
        this.configServer.addConfig("autoscaler", new XmlConfigSource<>(AutoScalingConfig.class, scalerConfig));

        this.localGroupsManager.loadGroups();
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
