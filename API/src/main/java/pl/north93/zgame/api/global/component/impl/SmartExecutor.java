package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import pl.north93.zgame.api.global.component.IBeanContext;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;

public class SmartExecutor
{
    public static Object execute(final Executable executable, final AbstractBeanContext beanContext, final Object instance)
    {
        final Parameter[] parameters = executable.getParameters();
        final Object[] execArgs = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++)
        {
            final Parameter parameter = parameters[i];

            final Class<?> type = parameter.getType();
            final Named namedAnn = parameter.getAnnotation(Named.class);

            // wstrzykujemy uzywany BeanContext
            if (IBeanContext.class.isAssignableFrom(type))
            {
                execArgs[i] = beanContext;
                continue;
            }

            final BeanQuery query = new BeanQuery().type(type);
            if (namedAnn != null)
            {
                query.name(namedAnn.value());
            }

            final Object bean = beanContext.getBean(query);
            execArgs[i] = bean;
        }

        executable.setAccessible(true);
        try
        {
            if (executable instanceof Method)
            {
                return ((Method) executable).invoke(instance, execArgs);
            }
            else if (executable instanceof Constructor)
            {
                return ((Constructor) executable).newInstance(execArgs);
            }
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException("Exception occurred in method wrapped by SmartExecutor", e);
        }

        throw new IllegalArgumentException("Executable must be method or constructor");
    }
}
