package pl.north93.zgame.api.global.utils;

public final class JavaUtils
{
    private JavaUtils()
    {
    }

    public static <T> T instanceOf(final Object object, final Class<T> clazz)
    {
        if (object == null)
        {
            return null;
        }
        if (clazz.isAssignableFrom(object.getClass()))
        {
            return (T) object;
        }
        return null;
    }
}
