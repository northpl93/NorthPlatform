package pl.north93.northplatform.controller.configserver;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.config.ConfigUpdatedNetEvent;
import pl.north93.northplatform.api.global.config.IConfig;
import pl.north93.northplatform.api.global.config.server.IConfigServerRpc;
import pl.north93.northplatform.api.global.redis.event.IEventManager;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.controller.configserver.source.IConfigSource;

@Slf4j
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
        log.info("Added new config with ID {}", configId);

        configImpl.reload();
        this.eventManager.callEvent(new ConfigUpdatedNetEvent(configId));
        return configImpl;
    }

    @Override
    public boolean reloadConfig(final String configId)
    {
        final ConfigImpl<?> config = this.configs.get(configId);
        if (config == null)
        {
            log.warn("Tried to reload unknown config {}", configId);
            return false;
        }

        config.reload();
        this.eventManager.callEvent(new ConfigUpdatedNetEvent(configId));
        log.info("Successfully reloaded config {}", configId);

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean updateConfig(final String configId, final Object newValue)
    {
        final ConfigImpl<Object> config = (ConfigImpl<Object>) this.configs.get(configId);
        if (config == null)
        {
            log.warn("Tried to update unknown config {}", configId);
            return false;
        }

        config.update(newValue);
        this.eventManager.callEvent(new ConfigUpdatedNetEvent(configId));
        log.info("Config with ID {} has been updated programmatically", config);

        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("configs", this.configs).toString();
    }
}
