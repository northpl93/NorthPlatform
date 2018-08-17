package pl.north93.zgame.api.global.metadata;

import java.time.Duration;
import java.time.Instant;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.serializer.platform.annotations.NorthField;

public final class MetaStore
{
    @NorthField(type = IdentityHashMap.class)
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

    public void setInstant(final MetaKey key, final Instant value)
    {
        this.set(key, value.toEpochMilli());
    }

    public Instant getInstant(final MetaKey key)
    {
        final Optional<Long> optionalEpochMilli = Optional.ofNullable(this.get(key));
        return optionalEpochMilli.map(Instant::ofEpochMilli).orElse(null);
    }

    public void setDuration(final MetaKey key, final Duration value)
    {
        this.set(key, value.toMillis());
    }

    public Duration getDuration(final MetaKey key)
    {
        final Optional<Long> optionalDuration = Optional.ofNullable(this.get(key));
        return optionalDuration.map(Duration::ofMillis).orElse(null);
    }

    public boolean contains(final MetaKey key)
    {
        return this.metadata.containsKey(key);
    }

    public Object remove(final MetaKey metaKey)
    {
        return this.metadata.remove(metaKey);
    }

    public void addAll(final MetaStore store)
    {
        this.metadata.putAll(store.metadata);
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
