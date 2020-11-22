package pl.north93.northplatform.daemon;

import com.google.common.eventbus.EventBus;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.network.daemon.DaemonRpc;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.global.utils.JaxbUtils;
import pl.north93.northplatform.daemon.cfg.DaemonConfig;
import pl.north93.northplatform.daemon.network.DaemonInfoHandler;
import pl.north93.northplatform.daemon.servers.ProcessWatchdog;

public class DaemonComponent extends Component
{
    @Inject
    private IRpcManager       rpcManager;
    @Inject
    private DaemonInfoHandler infoHandler;

    @Override
    protected void enableComponent()
    {
        this.rpcManager.addRpcImplementation(DaemonRpc.class, new DaemonRpcImpl());

        this.getApiCore().getHostConnector().runTaskAsynchronously(new ProcessWatchdog(), 20);
    }

    @Override
    protected void disableComponent()
    {
        this.infoHandler.delete();
    }

    @Bean
    private DaemonConfig daemonConfig()
    {
        return JaxbUtils.unmarshal(this.getApiCore().getFile("daemon.xml"), DaemonConfig.class);
    }

    @Named("daemon") @Bean
    private EventBus daemonEventBus()
    {
        return new EventBus("daemon");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
