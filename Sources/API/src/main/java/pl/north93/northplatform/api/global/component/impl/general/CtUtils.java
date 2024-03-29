package pl.north93.northplatform.api.global.component.impl.general;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import lombok.experimental.UtilityClass;
import pl.north93.northplatform.api.global.utils.lang.CatchException;

/**
 * Narzędzia do zamieniania obiektów z Javassista na Javowe wersje.
 */
@UtilityClass
public class CtUtils
{
    public Field toJavaField(final Class<?> clazz, final CtField ctField)
    {
        try
        {
            return clazz.getDeclaredField(ctField.getName());
        }
        catch ( Throwable e )
        {
            CatchException.sneaky(e);
            return null;
        }
    }

    public Method toJavaMethod(final Class<?> clazz, final CtMethod ctMethod)
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

    public Constructor<?> toJavaConstructor(final Class<?> clazz, final CtConstructor ctConstructor)
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

    public Class<?>[] ctTypesToJava(final ClassLoader classLoader, final CtClass[] classes)
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
