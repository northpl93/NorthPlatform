package pl.north93.zgame.api.bukkit.packets.wrappers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public abstract class AbstractWrapper
{
    protected static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    protected static MethodHandle unreflectGetter(final Class<?> clazz, final String name)
    {
        try
        {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return lookup.unreflectGetter(field);
        }
        catch (final IllegalAccessException | NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected static MethodHandle unreflectSetter(final Class<?> clazz, final String name)
    {
        try
        {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return lookup.unreflectSetter(field);
        }
        catch (final IllegalAccessException | NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }
}
