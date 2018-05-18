package pl.north93.zgame.api.global.utils;

import javax.annotation.Nonnull;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.finalizer.FinalizerUtils;

/**
 * Implementacja mapy bazujaca na HashMapie trzymajaca wartosci jako
 * Reference. Gdy GC usunie wartosc HashMapy spowoduje to takze usuniecie klucza.
 * Mapa nie zezwala na wartosci i klucze nullowe. Mapa jest wielowatkowo bezpieczna.
 *
 * Najlepiej jest uzywac tej mapy z metoda {@link #computeIfAbsent(Object, Function)}
 *
 * @param <K> Typ klucza.
 * @param <V> Typ wartosci.
 */
public class ReferenceHashMap<K, V> implements Map<K, V>
{
    private final ReferenceType        type;
    private final ReadWriteLock        lock = new ReentrantReadWriteLock();
    private final Map<K, Reference<V>> map  = new HashMap<>();

    public ReferenceHashMap()
    {
        this(ReferenceType.SOFT);
    }

    public ReferenceHashMap(final ReferenceType type)
    {
        Preconditions.checkNotNull(type);
        this.type = type;
    }

    @Override
    public int size()
    {
        try
        {
            this.lock.readLock().lock();
            return this.map.size();
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty()
    {
        try
        {
            this.lock.readLock().lock();
            return this.map.isEmpty();
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(final Object key)
    {
        Preconditions.checkNotNull(key);

        try
        {
            this.lock.readLock().lock();

            final Reference<V> reference = this.map.get(key);
            return reference != null && reference.get() != null;
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsValue(final Object value)
    {
        Preconditions.checkNotNull(value);

        try
        {
            this.lock.readLock().lock();

            for (final Reference<V> reference : this.map.values())
            {
                final V object = this.decodeReference(reference);
                if (object == value || value.equals(object))
                {
                    return true;
                }
            }

            return false;
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public V get(final Object key)
    {
        Preconditions.checkNotNull(key);

        try
        {
            this.lock.readLock().lock();
            return this.decodeReference(this.map.get(key));
        }
        finally
        {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public V put(final K key, final V value)
    {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);

        try
        {
            this.lock.writeLock().lock();

            final Reference<V> reference = this.createReference(key, value);
            return this.decodeReference(this.map.put(key, reference));
        }
        finally
        {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public V remove(final Object key)
    {
        Preconditions.checkNotNull(key);

        try
        {
            this.lock.writeLock().lock();
            return this.decodeReference(this.map.remove(key));
        }
        finally
        {
            this.lock.writeLock().unlock();
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
        try
        {
            this.lock.writeLock().lock();
            this.map.clear();
        }
        finally
        {
            this.lock.writeLock().unlock();
        }
    }

    @Nonnull
    @Override
    public Set<K> keySet()
    {
        // todo Trzeba wrapnac w wlasnego seta pilnujacego locka, aktualnie to jest zla implementacja
        return this.map.keySet();
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
        Preconditions.checkNotNull(key);

        try
        {
            this.lock.readLock().lock();

            final V value = this.tryGetValue(key);
            if (value != null)
            {
                return value;
            }
        }
        finally
        {
            this.lock.readLock().unlock();
        }

        try
        {
            this.lock.writeLock().lock();

            final V value = this.tryGetValue(key);
            if (value != null)
            {
                return value;
            }

            final V newValue = mappingFunction.apply(key);
            Preconditions.checkNotNull(newValue);

            this.map.put(key, this.createReference(key, newValue));
            return newValue;
        }
        finally
        {
            this.lock.writeLock().unlock();
        }
    }

    // metoda pomocnicza uzywana wyzej w computeIfAbsent
    // zeby nie kopiowac kodu
    private V tryGetValue(final K key)
    {
        final Reference<V> value = this.map.get(key);
        if (value != null)
        {
            return value.get();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Reference<V> createReference(final K key, final V value)
    {
        FinalizerUtils.register(value, () -> this.valueFinalized(key));
        return (Reference<V>) this.type.wrapper.apply(value);
    }

    private V decodeReference(final Reference<V> reference)
    {
        return Optional.ofNullable(reference).map(Reference::get).orElse(null);
    }

    private void valueFinalized(final K key)
    {
        try
        {
            this.lock.writeLock().lock();
            this.map.remove(key);
        }
        finally
        {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("map", this.map).toString();
    }

    public enum ReferenceType
    {
        WEAK(WeakReference::new),
        SOFT(SoftReference::new);

        private final Function<Object, Reference<Object>> wrapper;

        ReferenceType(final Function<Object, Reference<Object>> wrapper)
        {
            this.wrapper = wrapper;
        }
    }
}