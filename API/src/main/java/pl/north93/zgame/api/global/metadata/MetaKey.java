package pl.north93.zgame.api.global.metadata;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.collections.maps.CaseInsensitiveMap;

public final class MetaKey
{
    private static final Map<String, MetaKey> KEY_CACHE = new CaseInsensitiveMap<>();

    public static MetaKey get(final String keyName)
    {
        if (StringUtils.isEmpty(keyName))
        {
            throw new IllegalArgumentException("keyName can't be null");
        }
        return KEY_CACHE.computeIfAbsent(keyName, k -> new MetaKey(keyName));
    }

    // Class begin //

    private final String key;

    private MetaKey(final String key)
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

        final MetaKey metaKey = (MetaKey) o;

        return this.key.equals(metaKey.key);
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
