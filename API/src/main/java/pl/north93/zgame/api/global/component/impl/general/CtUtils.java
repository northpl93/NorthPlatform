package pl.north93.zgame.api.global.component.impl.general;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import pl.north93.zgame.api.global.utils.lang.SneakyThrow;

/**
 * Narzędzia do zamieniania obiektów z Javassista na Javowe wersje.
 */
public class CtUtils
{
    public static Field toJavaField(final Class<?> clazz, final CtField ctField)
    {
        try
        {
            return clazz.getDeclaredField(ctField.getName());
        }
        catch ( Throwable e )
        {
            SneakyThrow.sneaky(e);
            return null;
        }
    }

    public static Method toJavaMethod(final Class<?> clazz, final CtMethod ctMethod)
    {
        try
        {
            final Class<?>[] classes = ctTypesToJava(clazz.getClassLoader(), ctMethod.getParameterTypes());
            return clazz.getDeclaredMethod(ctMethod.getName(), classes);
        }
        catch (final Throwable e)
        {
            return null;
        }
    }

    public static Constructor toJavaConstructor(final Class<?> clazz, final CtConstructor ctConstructor)
    {
        try
        {
            final Class<?>[] classes = ctTypesToJava(clazz.getClassLoader(), ctConstructor.getParameterTypes());
            return clazz.getDeclaredConstructor(classes);
        }
        catch (final Throwable e)
        {
            return null;
        }
    }

    public static Class<?>[] ctTypesToJava(final ClassLoader classLoader, final CtClass[] classes)
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
