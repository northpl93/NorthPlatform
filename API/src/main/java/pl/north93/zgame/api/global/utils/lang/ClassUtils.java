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
}

class SecurityManagerHelper extends SecurityManager
{
    @Override
    public Class<?>[] getClassContext()
    {
        return super.getClassContext();
    }
}