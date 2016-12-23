package pl.north93.zgame.api.global.metadata;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.extra.MetaStoreTemplate;

@MsgPackCustomTemplate(MetaStoreTemplate.class)
public final class MetaStore
{
    private final Map<MetaKey, Object> metadata = new IdentityHashMap<>();

    public MetaStore()
    {
    }

    public void set(final MetaKey key, final Object value)
    {
        this.metadata.put(key, value);
    }

    public Object get(final MetaKey key)
    {
        return this.metadata.get(key);
    }

    public void setString(final MetaKey key, final String value)
    {
        this.set(key, value);
    }

    public String getString(final MetaKey key)
    {
        return (String) this.get(key);
    }

    public void setInteger(final MetaKey key, final Integer value)
    {
        this.set(key, value);
    }

    public Integer getInteger(final MetaKey key)
    {
        return (Integer) this.get(key);
    }

    public void setDouble(final MetaKey key, final Double value)
    {
        this.set(key, value);
    }

    public Double getDouble(final MetaKey key)
    {
        return (Double) this.get(key);
    }

    public Map<MetaKey, Object> getInternalMap()
    {
        return this.metadata;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("metadata", this.metadata).toString();
    }
}
