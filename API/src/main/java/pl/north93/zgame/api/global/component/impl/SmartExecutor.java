package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import pl.north93.zgame.api.global.component.annotations.bean.Named;

class SmartExecutor
{
    static Object execute(final Executable executable, final AbstractBeanContext beanContext, final Object instance)
    {
        final Parameter[] parameters = executable.getParameters();
        final Object[] execArgs = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++)
        {
            final Parameter parameter = parameters[i];

            final Class<?> type = parameter.getType();
            final Named namedAnn = type.getAnnotation(Named.class);

            final BeanQuery query = new BeanQuery().type(type);
            if (namedAnn != null)
            {
                query.name(namedAnn.value());
            }

            final Object bean = beanContext.getBean(query);
            execArgs[i] = bean;
        }

        if (executable instanceof Method)
        {
            try
            {
                return ((Method) executable).invoke(instance, execArgs);
            }
            catch (final IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        else if (executable instanceof Constructor)
        {
            try
            {
                return ((Constructor) executable).newInstance(execArgs);
            }
            catch (final InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        throw new IllegalArgumentException("Executable must be method or constructor");
    }
}
