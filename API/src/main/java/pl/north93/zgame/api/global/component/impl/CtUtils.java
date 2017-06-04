package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Method;

import javassist.CtClass;
import javassist.CtMethod;

class CtUtils
{
    static Method toJavaMethod(final Class<?> clazz, final CtMethod ctMethod)
    {
        try
        {
            final Class<?>[] classes = ctTypesToJava(clazz.getClassLoader(), ctMethod.getParameterTypes());
            return clazz.getDeclaredMethod(ctMethod.getName(), classes);
        }
        catch (final Throwable e)
        {
            throw new RuntimeException("Failed to convert CtMethod to java Method", e);
        }
    }

    static Class<?>[] ctTypesToJava(final ClassLoader classLoader, final CtClass[] classes)
    {
        final Class<?>[] out = new Class[classes.length];
        for (int i = 0; i < classes.length; i++)
        {
            try
            {
                out[i] = Class.forName(classes[i].getName(), true, classLoader);
            }
            catch (final ClassNotFoundException e)
            {
                throw new RuntimeException("Failed to convert array of CtClass to java Class", e);
            }
        }
        return out;
    }
}
