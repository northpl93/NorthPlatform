package pl.north93.zgame.api.global.serializer.platform.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.serializer.platform.InstanceCreator;
import rx.internal.util.unsafe.UnsafeAccess;

/*default*/ class UnsafeCreator<T> implements InstanceCreator<T>
{
    private final Class<T> clazz;

    public UnsafeCreator(final Class<T> clazz)
    {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T newInstance()
    {
        try
        {
            // todo jakis wlasny accessor do unsafe lub inna metoda alokacji instancji
            return (T) UnsafeAccess.UNSAFE.allocateInstance(this.clazz);
        }
        catch (final InstantiationException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).toString();
    }
}
