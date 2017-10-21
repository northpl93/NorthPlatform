package pl.north93.zgame.api.global.utils.lang;

import java.util.function.Function;

public final class ClassUtils
{
    private static final SecurityManagerHelper HELPER = new SecurityManagerHelper();
    
    private ClassUtils()
    {
    }
    
    public static Class<?> getCallerClass()
    {
        return getCallerClass(1);
    }
    
    public static Class<?> getCallerClass(int depth)
    {
        return HELPER.getClassContext()[depth + 3];
    }

    public static <T> T walkPackageInfo(final ClassLoader classLoader, String pack, final Function<Class<?>, T> packageInfo)
    {
        while (true)
        {
            try
            {
                final T result = packageInfo.apply(Class.forName(pack + ".package-info", false, classLoader));
                if (result != null)
                {
                    return result;
                }
            }
            catch (final ClassNotFoundException ignored)
            {
            }

            final int lastDot = pack.lastIndexOf('.');
            if (lastDot == - 1)
            {
                return null;
            }
            pack = pack.substring(0, lastDot);
        }
    }
}

class SecurityManagerHelper extends SecurityManager
{
    @Override
    public Class<?>[] getClassContext()
    {
        return super.getClassContext();
    }
}