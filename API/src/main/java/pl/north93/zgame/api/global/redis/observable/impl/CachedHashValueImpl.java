package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.Lock;

class CachedHashValueImpl<T> extends CachedValue<T>
{
    private final HashImpl<T> hash;
    private final String      name;
    private final Class<T>    clazz;
    private final Lock        myLock;
    private       T           cache;

    public CachedHashValueImpl(final ObservationManagerImpl observationManager, final HashImpl<T> hash, final String name, final Class<T> clazz)
    {
        super(observationManager);
        this.hash = hash;
        this.name = name;
        this.clazz = clazz;

        this.myLock = observationManager.getLock("caval_lock:" + this.getInternalName());
        observationManager.getValueSubHandler().addListener(this);
    }

    private synchronized T getFromRedis()
    {
        return this.cache = this.hash.get(this.name);
    }

    private void upload()
    {
        final byte[] serialized = this.observationManager.getMsgPack().serialize(this.clazz, this.cache);

        this.hash.put(this.name, serialized);
        this.observationManager.getValueSubHandler().update(this, serialized);
    }

    @Override
    public T get()
    {
        if (this.cache != null)
        {
            return this.cache;
        }
        return this.getFromRedis();
    }

    @Override
    public T getWithoutCache()
    {
        return this.getFromRedis();
    }

    @Override
    public T getOr(final Supplier<T> defaultValue)
    {
        if (this.isCached())
        {
            return this.cache;
        }
        else if (this.isAvailable())
        {
            return this.getFromRedis();
        }
        else
        {
            this.set(defaultValue.get());
            return this.cache;
        }
    }

    @Override
    public T getAndDelete()
    {
        final T value = this.hash.getAndDelete(this.name);
        if (value != null)
        {
            // czyscimy lokalne cache zeby nie czekac na update z redisa.
            this.cache = null;

            // wysylamy aktualizacje mowiaca o usunieciu wartosci.
            this.observationManager.getValueSubHandler().update(this, new byte[0]);
        }

        return value;
    }

    @Override
    public boolean update(final Function<T, T> update)
    {
        try (final Lock lock = this.lock())
        {
            final T t = this.get();
            if (t != null)
            {
                this.set(update.apply(t));
                return true;
            }

            return false;
        }
    }

    @Override
    public void get(final Consumer<T> callback)
    {
        if (this.isCached())
        {
            callback.accept(this.cache);
        }
        else
        {
            this.observationManager.getPlatformConnector().runTaskAsynchronously(() -> callback.accept(this.getFromRedis()));
        }
    }

    @Override
    public void ifPresent(final Consumer<T> action)
    {
        final T value = this.get();
        if (value != null)
        {
            action.accept(value);
        }
    }

    @Override
    public void set(final T newValue)
    {
        if (newValue == null)
        {
            this.delete();
        }
        else
        {
            this.cache = newValue;
            this.upload();
        }
    }

    @Override
    public void setExpire(final T newValue, final long time, final TimeUnit timeUnit)
    {
        throw new UnsupportedOperationException("Expire is not supported in hash value.");
    }

    @Override
    public boolean delete()
    {
        return this.hash.delete(this.name);
    }

    @Override
    public boolean isAvailable()
    {
        return this.hash.exists(this.name);
    }

    @Override
    public boolean isCached()
    {
        return this.cache != null;
    }

    @Override
    public boolean expire(final int seconds)
    {
        throw new UnsupportedOperationException("Expire is not supported in hash value.");
    }

    @Override
    public long getTimeToLive()
    {
        throw new UnsupportedOperationException("Expire is not supported in hash value.");
    }

    @Override
    public final Lock getLock()
    {
        return this.myLock;
    }

    @Override
    String getInternalName()
    {
        return "hash:" + this.hash.getName() + ":" + this.name;
    }

    @Override
    void handleNewValue(final byte[] newValue)
    {
        if (newValue.length == 0)
        {
            this.cache = null;
        }
        else
        {
            this.cache = this.observationManager.getMsgPack().deserialize(this.clazz, newValue);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("hash", this.hash).append("name", this.name).append("clazz", this.clazz).toString();
    }
}
