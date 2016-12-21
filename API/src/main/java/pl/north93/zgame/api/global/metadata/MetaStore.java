package pl.north93.zgame.api.global.metadata;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class MetaStore
{
    private static final Map<Object, MetaStore> CACHE = new WeakHashMap<>();

    public static MetaStore getFor(final Metadatable metadatableObject, final MetaStore parent)
    {
        return CACHE.computeIfAbsent(metadatableObject, k -> new MetaStore(parent));
    }

    public static MetaStore getFor(final Metadatable metadatableObject)
    {
        return getFor(metadatableObject, null);
    }

    public static MetaStore getWithoutCache(final MetaStore parent)
    {
        return new MetaStore(parent);
    }

    // class begin //

    private final Map<MetaKey, Object> metadata = new IdentityHashMap<>();
    private final MetaStore            parent;

    private MetaStore(final MetaStore parent)
    {
        this.parent = parent;
    }

    public boolean hasParent()
    {
        return this.parent != null;
    }

    public void set(final MetaKey key, final Object value)
    {
        this.metadata.put(key, value);
    }

    public Object get(final MetaKey key)
    {
        final Object fromThis = this.metadata.get(key);
        if (fromThis == null)
        {
            if (this.parent != null)
            {
                return this.parent.get(key);
            }
        }
        return fromThis;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("metadata", this.metadata).append("parent", this.parent).toString();
    }
}
