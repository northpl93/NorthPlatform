package pl.north93.northplatform.api.global.utils.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassUtils
{
    private static final SecurityManagerHelper HELPER = new SecurityManagerHelper();
    
    private ClassUtils()
    {
    }

    public static Logger getCallingLogger()
    {
        final Class<?> callerClass = getCallerClass(2);
        return LoggerFactory.getLogger(callerClass);
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