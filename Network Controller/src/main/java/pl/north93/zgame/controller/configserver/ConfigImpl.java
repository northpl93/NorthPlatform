package pl.north93.zgame.controller.configserver;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.controller.configserver.source.IConfigSource;

class ConfigImpl<T> implements IConfig<T>
{
    private final IObservationManager observationManager;
    private final String              id;
    private final IConfigSource<T>    configSource;
    private       T                   cached;

    public ConfigImpl(final IObservationManager observationManager, final String id, final IConfigSource<T> configSource)
    {
        this.observationManager = observationManager;
        this.id = id;
        this.configSource = configSource;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public T get()
    {
        return this.cached;
    }

    private Value<T> getValue()
    {
        return this.observationManager.get(this.configSource.getType(), "configs_" + this.id);
    }

    @Override
    public void reload()
    {
        this.cached = this.configSource.load();
        this.getValue().set(this.cached);
    }

    public void delete()
    {
        this.getValue().delete();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("configSource", this.configSource).append("cached", this.cached).toString();
    }
}
