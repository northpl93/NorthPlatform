package pl.north93.northplatform.api.global.serializer.platform.impl;

import pl.north93.northplatform.api.global.serializer.platform.ClassResolver;
import pl.north93.northplatform.api.global.utils.lang.SneakyThrow;

/*default*/ class DefaultClassResolver implements ClassResolver
{
    @Override
    public Class<?> findClass(final String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (final ClassNotFoundException e)
        {
            SneakyThrow.sneaky(e);
            return null;
        }
    }
}
