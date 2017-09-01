package pl.north93.zgame.daemon;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.api.global.network.daemon.DaemonDto;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.daemon.cfg.DaemonConfig;
import pl.north93.zgame.daemon.servers.ServersManager;

public class DaemonComponent extends Component
{
    @Inject
    private IRpcManager      rpcManager;
    private DaemonConfig     config;
    private Value<DaemonDto> daemonInfo;
    private ServersManager   serversManager;

    @Override
    protected void enableComponent()
    {
        this.config = loadConfigFile(DaemonConfig.class, API.getFile("daemon.yml"));
        final DaemonDto daemon = DaemonDto.builder().setName(this.getApiCore().getId())
                                          .setHostName(this.getApiCore().getHostName())
                                          .setMaxRam(this.config.maxMemory)
                                          .setRamUsed(0)
                                          .setServerCount(0)
                                          .setAcceptingServers(true)
                                          .build();

        final IObservationManager observation = this.getApiCore().getComponentManager().getComponent("API.Database.Redis.Observer");
        this.daemonInfo = observation.of(daemon);

        this.rpcManager.addRpcImplementation(DaemonRpc.class, new DaemonRpcImpl(this));
        this.serversManager = new ServersManager();
        this.serversManager.startServerManager();
    }

    @Override
    protected void disableComponent()
    {
        this.daemonInfo.delete();
        this.serversManager.stopServerManager();
    }

    public Value<DaemonDto> getDaemonInfo()
    {
        return this.daemonInfo;
    }

    public ServersManager getServersManager()
    {
        return this.serversManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("config", this.config).toString();
    }
}
