package pl.north93.northplatform.api.global.serializer.platform.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.serializer.platform.InstanceCreator;

/*default*/ class MethodHandleConstructorCreator<T> implements InstanceCreator<T>
{
    private static final Lookup       LOOKUP      = MethodHandles.lookup();
    private static final MethodType   VOID_TYPE   = MethodType.methodType(void.class);
    private static final MethodType   OBJECT_TYPE = MethodType.methodType(Object.class);
    private final        MethodHandle constructor;

    public MethodHandleConstructorCreator(final Class<T> clazz)
    {
        try
        {
            this.constructor = LOOKUP.findConstructor(clazz, VOID_TYPE).asType(OBJECT_TYPE);
        }
        catch (final NoSuchMethodException | IllegalAccessException e)
        {
            throw new RuntimeException("Lookup failed. Can't find constructor.", e);
        }
    }

    @Override
    public T newInstance()
    {
        try
        {
            //noinspection unchecked
            return (T) this.constructor.invokeExact();
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("constructor", this.constructor).toString();
    }
}
