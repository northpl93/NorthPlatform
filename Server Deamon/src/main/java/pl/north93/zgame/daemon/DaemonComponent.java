package pl.north93.zgame.daemon;

import javax.xml.bind.JAXB;

import com.google.common.eventbus.EventBus;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.daemon.DaemonDto;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.daemon.cfg.DaemonConfig;
import pl.north93.zgame.daemon.servers.ProcessWatchdog;

public class DaemonComponent extends Component
{
    @Inject
    private IRpcManager      rpcManager;
    @Inject
    private INetworkManager  networkManager;
    @Inject
    private DaemonConfig     config;
    private Value<DaemonDto> daemonInfo;

    @Override
    protected void enableComponent()
    {
        final DaemonDto daemon = new DaemonDto(this.getApiCore().getId(), this.getApiCore().getHostName(), this.config.maxMemory, 0, 0, true);

        final Hash<DaemonDto> daemons = this.networkManager.getDaemons().unsafe().getHash();
        daemons.put(this.getApiCore().getId(), daemon);
        this.daemonInfo = daemons.getAsValue(this.getApiCore().getId());

        this.rpcManager.addRpcImplementation(DaemonRpc.class, new DaemonRpcImpl());

        this.getApiCore().getPlatformConnector().runTaskAsynchronously(new ProcessWatchdog(), 20);
    }

    @Override
    protected void disableComponent()
    {
        this.daemonInfo.delete();
    }

    public Value<DaemonDto> getDaemonInfo()
    {
        return this.daemonInfo;
    }

    @Bean
    private DaemonConfig daemonConfig()
    {
        return JAXB.unmarshal(this.getApiCore().getFile("daemon.xml"), DaemonConfig.class);
    }

    @Named("daemon") @Bean
    private EventBus daemonEventBus()
    {
        return new EventBus("daemon");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("config", this.config).toString();
    }
}
