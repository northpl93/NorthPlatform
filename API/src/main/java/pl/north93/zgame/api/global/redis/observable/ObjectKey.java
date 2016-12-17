package pl.north93.zgame.api.global.redis.observable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class ObjectKey
{
    private final String key;

    public ObjectKey(final String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return this.key;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final ObjectKey objectKey = (ObjectKey) o;

        return this.key.equals(objectKey.key);
    }

    @Override
    public int hashCode()
    {
        return this.key.hashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("key", this.key).toString();
    }
}
