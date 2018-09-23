package pl.north93.zgame.api.global.config.client;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.server.IConfigServerRpc;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;

@Slf4j
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

    @Override
    public void reloadConfig(final String id)
    {
        if (this.serverRpc.reloadConfig(id))
        {
            log.info("Received information about successfully reload of config {}", id);
        }
    }

    public void updateConfig(final String id, final Object newValue)
    {
        if (this.serverRpc.updateConfig(id, newValue))
        {
            log.info("Received information about successfully update of config {}", id);
        }
    }

    @Override
    public <T> IConfig<T> getConfig(final Class<T> type, final String configId)
    {
        return new ConfigImpl<>(this, configId, this.observationManager.get(type, "configs_" + configId));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
