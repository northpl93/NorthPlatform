package pl.north93.zgame.controller.configserver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.ConfigUpdatedNetEvent;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.server.IConfigServerRpc;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.controller.configserver.source.IConfigSource;

public class ConfigServerComponent extends Component implements IConfigServer, IConfigServerRpc
{
    @Inject
    private IRpcManager   rpcManager;
    @Inject
    private IEventManager eventManager;
    @Inject
    private IObservationManager observationManager;
    private final Map<String, ConfigImpl<?>> configs = new HashMap<>();

    @Override
    protected void enableComponent()
    {
        this.rpcManager.addRpcImplementation(IConfigServerRpc.class, this);
    }

    @Override
    protected void disableComponent()
    {
        this.configs.values().forEach(ConfigImpl::delete);
        this.configs.clear();
    }

    @Override
    public <T> IConfig<T> addConfig(final String configId, final IConfigSource<T> loader)
    {
        final ConfigImpl<T> configImpl = new ConfigImpl<>(this.observationManager, configId, loader);
        this.configs.put(configId, configImpl);
        this.getLogger().log(Level.INFO, "Added new config with ID {0}", configId);

        configImpl.reload();
        this.eventManager.callEvent(new ConfigUpdatedNetEvent(configId));
        return configImpl;
    }

    @Override
    public Boolean reloadConfig(final String configId)
    {
        this.configs.get(configId).reload();
        this.eventManager.callEvent(new ConfigUpdatedNetEvent(configId));
        this.getLogger().log(Level.INFO, "Successfully reloaded config {0}", configId);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean updateConfig(final String configId, final Object newValue)
    {
        final ConfigImpl<Object> config = (ConfigImpl<Object>) this.configs.get(configId);
        config.update(newValue);
        this.eventManager.callEvent(new ConfigUpdatedNetEvent(configId));
        this.getLogger().log(Level.INFO, "Config with ID {0} has been updated programmatically", config);
        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("configs", this.configs).toString();
    }
}
