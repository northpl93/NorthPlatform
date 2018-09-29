package pl.north93.zgame.api.global.serializer.platform.impl;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/*default*/ final class UnsafeAccess
{
    private static final Object UNSAFE;

    static
    {
        Object unsafe;
        try
        {
            final Class<?> unsafeClazz = Class.forName("sun.misc.Unsafe");
            final Field theUnsafe = unsafeClazz.getDeclaredField("theUnsafe");

            theUnsafe.setAccessible(true);
            unsafe = theUnsafe.get(null);
        }
        catch (final Exception e)
        {
            unsafe = null;
        }

        UNSAFE = unsafe;
    }

    public static boolean isUnsafeSupported()
    {
        return UNSAFE != null;
    }

    public static Unsafe getUnsafe()
    {
        return (Unsafe) UNSAFE;
    }
}
