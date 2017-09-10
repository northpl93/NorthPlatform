package pl.north93.zgame.api.global.utils;

import javax.annotation.Nonnull;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Implementacja mapy bazujaca na HashMapie trzymajaca wartosci jako
 * SoftReference. Gdy zostanie wykonane finalize wtedy klucz do ktorego
 * przypisana jest wartosc usuwa sie. Wszystkie wywolania sa synchronizowane
 * wiec jest wielowatkowo bezpieczna.
 *
 * Najlepiej jest uzywac tej mapy z metoda {@link #computeIfAbsent(Object, Function)}
 *
 * @param <K> Typ klucza.
 * @param <V> Typ wartosci.
 */
public class SoftValueHashMap<K, V> implements Map<K, V>
{
    private final Map<K, SoftReference<WeakObjectHolder>> map = new HashMap<>();

    @Override
    public int size()
    {
        synchronized (this.map)
        {
            return this.map.size();
        }
    }

    @Override
    public boolean isEmpty()
    {
        synchronized (this.map)
        {
            return this.map.isEmpty();
        }
    }

    @Override
    public boolean containsKey(final Object key)
    {
        synchronized (this.map)
        {
            return this.map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(final Object value)
    {
        throw new NotImplementedException("entrySet"); // todo
    }

    @Override
    public V get(final Object key)
    {
        synchronized (this.map)
        {
            return this.decodeReference(this.map.get(key));
        }
    }

    @Override
    public V put(final K key, final V value)
    {
        synchronized (this.map)
        {
            return this.decodeReference(this.map.put(key, new SoftReference<>(new WeakObjectHolder(key, value))));
        }
    }

    @Override
    public V remove(final Object key)
    {
        synchronized (this.map)
        {
            return this.decodeReference(this.map.remove(key));
        }
    }

    @Override
    public void putAll(@Nonnull final Map<? extends K, ? extends V> m)
    {
        // nie trzeba synchronizowac bo this.put juz to robi
        for (final Entry<? extends K, ? extends V> entry : m.entrySet())
        {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear()
    {
        synchronized (this.map)
        {
            this.map.clear();
        }
    }

    @Nonnull
    @Override
    public Set<K> keySet()
    {
        synchronized (this.map)
        {
            return this.map.keySet();
        }
    }

    @Nonnull
    @Override
    public Collection<V> values()
    {
        throw new NotImplementedException("entrySet"); // todo
    }

    @Nonnull
    @Override
    public Set<Entry<K, V>> entrySet()
    {
        throw new NotImplementedException("entrySet"); // todo
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction)
    {
        synchronized (this.map)
        {
            SoftReference<WeakObjectHolder> value = this.map.get(key);
            if (value == null)
            {
                final V newValue = mappingFunction.apply(key);
                value = new SoftReference<>(new WeakObjectHolder(key, newValue));
                this.map.put(key, value);

                return newValue;
            }
            else
            {
                final WeakObjectHolder weakHolder = value.get();
                if (weakHolder == null)
                {
                    final V newValue = mappingFunction.apply(key);
                    value = new SoftReference<>(new WeakObjectHolder(key, newValue));
                    this.map.put(key, value);

                    return newValue;
                }
                else
                {
                    return weakHolder.value;
                }
            }
        }
    }

    private V decodeReference(final SoftReference<WeakObjectHolder> reference)
    {
        if (reference == null)
        {
            return null;
        }

        final WeakObjectHolder weakObjectHolder = reference.get();
        if (weakObjectHolder == null)
        {
            return null;
        }

        return weakObjectHolder.value;
    }

    private void valueFinalized(final K key)
    {
        synchronized (this.map)
        {
            this.map.remove(key);
        }
    }

    // ten obiekt musi byc trzymany jako WeakReference.
    // wtedy moze zostac bez problemu usuniety i zostanie wykonane finalize
    private final class WeakObjectHolder
    {
        private final K key;
        private final V value;

        private WeakObjectHolder(final K key, final V value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        protected void finalize() throws Throwable
        {
            SoftValueHashMap.this.valueFinalized(this.key);
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("key", this.key).append("value", this.value).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("map", this.map).toString();
    }
}