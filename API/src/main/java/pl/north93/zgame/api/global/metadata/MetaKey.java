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
        return get(keyName, true);
    }

    public static MetaKey get(final String keyName, final boolean persist)
    {
        if (StringUtils.isEmpty(keyName))
        {
            throw new IllegalArgumentException("keyName can't be null");
        }
        final MetaKey metaKey = KEY_CACHE.computeIfAbsent(keyName, k -> new MetaKey(keyName, persist));
        if (metaKey.persist != persist)
        {
            throw new IllegalArgumentException("Key " + keyName + " can't change persist state after creation!");
        }
        return metaKey;
    }

    // Class begin //

    private final String  key;
    private final boolean persist;

    private MetaKey(final String key, final boolean persist)
    {
        this.key = key;
        this.persist = persist;
    }

    public String getKey()
    {
        return this.key;
    }

    public boolean isPersist()
    {
        return this.persist;
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

        return this.persist == metaKey.persist && this.key.equals(metaKey.key);
    }

    @Override
    public int hashCode()
    {
        int result = this.key.hashCode();
        result = 31 * result + (this.persist ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("key", this.key).toString();
    }
}
