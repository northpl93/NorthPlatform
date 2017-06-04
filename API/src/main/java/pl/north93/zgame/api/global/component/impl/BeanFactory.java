package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Named;

class BeanFactory
{
    public static final BeanFactory INSTANCE = new BeanFactory();

    private BeanFactory()
    {
    }

    /*default*/ void createStaticBean(final AbstractBeanContext beanContext, final Object object)
    {
        final Class<?> beanType;
        final String name;
        final Object bean;

        if (object instanceof Executable)
        {
            final Executable executable = (Executable) object;
            beanType = this.getReturnType(executable);

            final Named namedAnnotation = executable.getAnnotation(Named.class);
            if (namedAnnotation != null)
            {
                name = namedAnnotation.value();
            }
            else
            {
                name = beanType.getName();
            }

            final Object instance;
            if (object instanceof Method && Modifier.isStatic(executable.getModifiers()))
            {
                instance = null;
            }
            else
            {
                instance = beanContext.getBean(executable.getDeclaringClass());
            }

            bean = SmartExecutor.execute(executable, beanContext, instance);
        }
        else if (object instanceof Class)
        {
            beanType = (Class) object;

            if (! beanType.isAnnotationPresent(Bean.class))
            {
                throw new IllegalArgumentException("Bean must be annotated by @Bean");
            }

            final Named namedAnnotation = beanType.getAnnotation(Named.class);
            if (namedAnnotation != null)
            {
                name = namedAnnotation.value();
            }
            else
            {
                name = beanType.getName();
            }

            final Constructor<?> constructor = beanType.getConstructors()[0];
            bean = SmartExecutor.execute(constructor, beanContext, null);
        }
        else
        {
            throw new IllegalArgumentException("Object must be Executable");
        }

        beanContext.add(new StaticBeanContainer(beanType, name, bean));
    }

    /*default*/ void createDynamicBean(final AbstractBeanContext beanContext, final Object object)
    {
        if (object instanceof Method)
        {
            final Method method = (Method) object;

            final Class<?> type = method.getReturnType();
            final String name;

            final Named namedAnnotation = method.getAnnotation(Named.class);
            if (namedAnnotation != null)
            {
                name = namedAnnotation.value();
            }
            else
            {
                name = type.getName();
            }

            beanContext.add(new DynamicBeanContainer(type, name, beanContext, method));
        }
        else
        {
            throw new IllegalArgumentException("Object must be Method");
        }
    }

    private Class<?> getReturnType(final Executable executable)
    {
        if (executable instanceof Method)
        {
            return ((Method) executable).getReturnType();
        }
        else if (executable instanceof Constructor)
        {
            return executable.getDeclaringClass();
        }
        throw new AssertionError("Will never happen.");
    }
}
