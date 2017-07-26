package pl.north93.zgame.api.global.config;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.event.INetEvent;

/**
 * Event sieciowy wywolywany gdy config zostanie zaladowany
 * lub przeladowany.
 */
public class ConfigUpdatedNetEvent implements INetEvent
{
    private String configName;

    public ConfigUpdatedNetEvent() // serialization
    {
    }

    public ConfigUpdatedNetEvent(final String configName)
    {
        this.configName = configName;
    }

    public String getConfigName()
    {
        return this.configName;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("configName", this.configName).toString();
    }
}
