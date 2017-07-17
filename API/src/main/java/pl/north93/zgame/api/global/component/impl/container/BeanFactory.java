package pl.north93.zgame.api.global.component.impl.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.diorite.utils.lazy.LazyValue;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.component.exceptions.BeanCreationException;
import pl.north93.zgame.api.global.component.impl.SmartExecutor;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;

public class BeanFactory
{
    public static final BeanFactory INSTANCE = new BeanFactory();

    private BeanFactory()
    {
    }

    public void createStaticBean(final AbstractBeanContext beanContext, final Object object) throws BeanCreationException
    {
        //System.out.println("createStaticBean(" + beanContext.getBeanContextName() + ", " + object + "");
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
            if (object instanceof Constructor || object instanceof Method && Modifier.isStatic(executable.getModifiers()))
            {
                instance = null;
            }
            else
            {
                instance = beanContext.getBean(executable.getDeclaringClass());
            }

            try
            {
                bean = SmartExecutor.execute(executable, beanContext, instance);
            }
            catch (final Exception e)
            {
                throw new BeanCreationException(beanType, e);
            }
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
            try
            {
                bean = SmartExecutor.execute(constructor, beanContext, null);
            }
            catch (final Exception e)
            {
                throw new BeanCreationException(beanType, e);
            }
        }
        else
        {
            throw new IllegalArgumentException("Object must be Executable");
        }

        if (bean == null)
        {
            throw new RuntimeException("Bean creator returned null instance.");
        }

        //System.out.println("beanContext:" + beanContext);
        //System.out.println("bean:" + bean);

        beanContext.add(new StaticBeanContainer(beanType, name, bean));
    }

    public void createStaticBeanManually(final AbstractBeanContext beanContext, final Object bean)
    {
        final Class<? extends AbstractBeanContext> aClass = beanContext.getClass();
        this.createStaticBeanManually(beanContext, aClass, aClass.getName(), bean);
    }

    public void createStaticBeanManually(final AbstractBeanContext beanContext, final Class<?> type, final String name, final Object bean)
    {
        beanContext.add(new StaticBeanContainer(type, name, bean));
    }

    public void createDynamicBean(final AbstractBeanContext beanContext, final Object object)
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

    public void createLazyBean(final AbstractBeanContext beanContext, final Class<?> type, final String name, final LazyValue object)
    {
        final LazyBeanContainer lazyBeanContainer = new LazyBeanContainer(type, name, object);
        beanContext.add(lazyBeanContainer);
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
