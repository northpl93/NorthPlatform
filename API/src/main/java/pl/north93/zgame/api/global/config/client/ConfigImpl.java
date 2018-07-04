package pl.north93.zgame.api.global.config.client;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.redis.observable.Value;

class ConfigImpl<T> implements IConfig<T>
{
    private final ConfigClientImpl client;
    private final String           id;
    private final Value<T>         config;

    public ConfigImpl(final ConfigClientImpl client, final String id, final Value<T> config)
    {
        this.client = client;
        this.id = id;
        this.config = config;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public T get()
    {
        return this.config.get();
    }

    @Override
    public Optional<T> getOptional()
    {
        return this.config.getOptional();
    }

    @Override
    public void update(final T newValue)
    {
        this.client.updateConfig(this.id, newValue);
    }

    @Override
    public void reload()
    {
        this.client.reloadConfig(this.id);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("config", this.config).toString();
    }
}
