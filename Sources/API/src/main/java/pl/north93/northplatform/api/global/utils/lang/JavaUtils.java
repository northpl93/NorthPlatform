package pl.north93.northplatform.api.global.utils.lang;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JavaUtils
{
    @SuppressWarnings("unchecked")
    public <T> T instanceOf(final Object object, final Class<T> clazz)
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
