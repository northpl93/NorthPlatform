package pl.north93.northplatform.api.global.redis.observable.impl;

import java.util.concurrent.TimeUnit;

import lombok.ToString;
import pl.north93.northplatform.api.global.redis.observable.Lock;

@ToString(of = {"hash", "name", "clazz"})
class CachedHashValueImpl<T> extends CachedValue<T>
{
    private final HashImpl<T> hash;
    private final String name;
    private final Class<T> clazz;
    private final Lock myLock;
    private T cache;

    public CachedHashValueImpl(final ObservationManagerImpl observationManager, final HashImpl<T> hash, final String name, final Class<T> clazz)
    {
        super(observationManager);
        this.hash = hash;
        this.name = name;
        this.clazz = clazz;

        this.myLock = observationManager.getLock("caval_lock:" + this.getInternalName());
        observationManager.getValueSubHandler().registerListener(this);
    }

    private synchronized T getFromRedis()
    {
        return this.cache = this.hash.get(this.name);
    }

    private void upload()
    {
        final byte[] serialized = this.observationManager.getMsgPack().serialize(this.clazz, this.cache);

        this.hash.put(this.name, serialized);
        this.observationManager.getValueSubHandler().broadcastUpdate(this, serialized);
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
    public T getAndDelete()
    {
        final T value = this.hash.getAndDelete(this.name);
        if (value != null)
        {
            // czyscimy lokalne cache zeby nie czekac na update z redisa.
            this.cache = null;

            // wysylamy aktualizacje mowiaca o usunieciu wartosci.
            this.observationManager.getValueSubHandler().broadcastUpdate(this, new byte[0]);
        }

        return value;
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
}
