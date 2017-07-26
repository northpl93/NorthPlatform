package pl.north93.zgame.api.global.config.client;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.server.IConfigServerRpc;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;

public class ConfigClientImpl extends Component implements IConfigClient
{
    @Inject
    private IObservationManager observationManager;
    @Inject
    private IRpcManager         rpcManager;
    private IConfigServerRpc    serverRpc;

    @Override
    protected void enableComponent()
    {
        this.serverRpc = this.rpcManager.createRpcProxy(IConfigServerRpc.class, Targets.networkController());
    }

    @Override
    protected void disableComponent()
    {
    }

    public void reloadConfig(final String id)
    {
        this.serverRpc.reloadConfig(id);
    }

    @Override
    public <T> IConfig<T> getConfig(final Class<T> type, final String id)
    {
        return new ConfigImpl<>(this, id, this.observationManager.get(type, "configs_" + id));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
