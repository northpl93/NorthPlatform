package pl.north93.zgame.api.global.serializer.platform.impl;

import java.util.Map;
import java.util.WeakHashMap;

import lombok.ToString;
import pl.north93.zgame.api.global.serializer.platform.InstanceCreator;

@ToString
/*default*/ class InstantiationManager
{
    private final Map<Class<?>, InstanceCreator> instanceCreators = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> InstanceCreator<T> getInstanceCreator(final Class<T> clazz)
    {
        return this.instanceCreators.computeIfAbsent(clazz, this::setUpCreator);
    }

    private <T> InstanceCreator<T> setUpCreator(final Class<T> clazz)
    {
        try
        {
            clazz.getConstructor(); // probujemy uzyskac konstruktor bez argumentów
            return new MethodHandleConstructorCreator<>(clazz);
        }
        catch (final Exception e)
        {
            if (UnsafeAccess.isUnsafeSupported())
            {
                return new UnsafeCreator<>(clazz);
            }

            throw new UnsupportedOperationException("Unsafe is unsupported so class must have no-args constructor");
        }
    }
}
