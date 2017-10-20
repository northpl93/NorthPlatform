package pl.north93.zgame.api.global.utils.lang;

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

    public static Package getParent(final Package pack)
    {
        String name = pack.getName();
        Package parent;
        do
        {
            final int lastDot = name.lastIndexOf('.');
            if (lastDot == -1)
            {
                return Package.getPackage(name);
            }
            else
            {
                name = name.substring(0, lastDot);
                parent = Package.getPackage(name);
            }
        }
        while (parent == null);
        return parent;
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