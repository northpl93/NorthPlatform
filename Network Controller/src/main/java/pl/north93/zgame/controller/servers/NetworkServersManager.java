package pl.north93.zgame.controller.servers;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.controller.configserver.IConfigServer;
import pl.north93.zgame.controller.configserver.source.XmlConfigSource;
import pl.north93.zgame.api.global.network.daemon.config.AutoScalingConfig;
import pl.north93.zgame.controller.servers.groups.LocalGroupsManager;

public class NetworkServersManager extends Component implements INetworkServersManager
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
