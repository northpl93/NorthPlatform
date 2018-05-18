package pl.north93.zgame.api.global.metadata;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.extra.MetaStoreTemplate;

@MsgPackCustomTemplate(MetaStoreTemplate.class)
public final class MetaStore
{
    private final Map<MetaKey, Object> metadata = new IdentityHashMap<>();

    /**
     * Konstruktor tworzący nowe, puste {@link MetaStore}.
     */
    public MetaStore()
    {
    }

    /**
     * Konstruktor wykonujący płytkie kopiowanie
     * (wartości wewnątrz mapy będą tymi samymi referencjami).
     *
     * @param store Inny MetaStore do skopiowania danych.
     */
    public MetaStore(final MetaStore store)
    {
        this.metadata.putAll(store.metadata);
    }

    public void set(final MetaKey key, final Object value)
    {
        this.metadata.put(key, value);
    }

    /**
     * Metoda zwracająca wartość z metastore, wykonująca castowanie przez genericsy.
     *
     * @param key Klucz dla którego wartość pobieramy.
     * @param <T> Typ obiektu który uzyskujemy z MetaStore.
     * @return Wartość pobrana z wewnętrznej Mapy.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final MetaKey key)
    {
        return (T) this.metadata.get(key);
    }

    public void setString(final MetaKey key, final String value)
    {
        this.set(key, value);
    }

    public String getString(final MetaKey key)
    {
        return this.get(key);
    }

    public void setInteger(final MetaKey key, final Integer value)
    {
        this.set(key, value);
    }

    public Integer getInteger(final MetaKey key)
    {
        return this.get(key);
    }

    public void setDouble(final MetaKey key, final Double value)
    {
        this.set(key, value);
    }

    public Double getDouble(final MetaKey key)
    {
        return this.get(key);
    }

    public void setBoolean(final MetaKey key, final Boolean value)
    {
        this.set(key, value);
    }

    public Boolean getBoolean(final MetaKey key)
    {
        return this.get(key);
    }

    public void setUuid(final MetaKey key, final UUID value)
    {
        this.set(key, value);
    }

    public UUID getUuid(final MetaKey key)
    {
        return this.get(key);
    }

    public void setLong(final MetaKey key, final long value)
    {
        this.set(key, value);
    }

    public long getLong(final MetaKey key)
    {
        return this.get(key);
    }

    public boolean contains(final MetaKey key)
    {
        return this.metadata.containsKey(key);
    }

    public Object remove(final MetaKey metaKey)
    {
        return this.metadata.remove(metaKey);
    }

    public Map<MetaKey, Object> getInternalMap()
    {
        return this.metadata;
    }

    /**
     * Tworzy nowy MetaStore z usuniętymi wsystkimi kluczami które nie są trwałe.
     *
     * @see MetaKey#isPersist()
     * @return Nowa instancja MetaStore z samymi trwałymi kluczami.
     */
    public MetaStore prepareForPersist()
    {
        final MetaStore newMetaStore = new MetaStore();
        for (final Map.Entry<MetaKey, Object> entry : this.metadata.entrySet())
        {
            final MetaKey key = entry.getKey();
            if (! key.isPersist())
            {
                continue;
            }

            newMetaStore.set(key, entry.getValue());
        }

        return newMetaStore;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("metadata", this.metadata).toString();
    }
}
