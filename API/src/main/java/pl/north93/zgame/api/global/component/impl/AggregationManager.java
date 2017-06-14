package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.lazy.LazyValue;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.context.TemporaryBeanContext;

public class AggregationManager
{
    private Multimap<Class<?>, Method> listeners = ArrayListMultimap.create();

    public void addAggregator(final Method method)
    {
        final Aggregator aggregatorAnn = method.getAnnotation(Aggregator.class);

        this.listeners.put(aggregatorAnn.value(), method);
    }

    public void call(final AbstractBeanContext beanContext, final Class<?> clazz)
    {
        if (clazz.isAnnotationPresent(SkipInjections.class))
        {
            return;
        }

        final LazyValue<Object> object = new LazyValue<>(() -> this.getInstance(beanContext, clazz));

        for (final Class<?> aClass : this.getPossibleAggregationPoints(clazz))
        {
            final Collection<Method> methods = this.listeners.get(aClass);
            for (final Method method : methods)
            {
                final TemporaryBeanContext tempContext = new TemporaryBeanContext(beanContext);
                tempContext.put(aClass, object.get());

                if (Modifier.isStatic(method.getModifiers()))
                {
                    SmartExecutor.execute(method, tempContext, null);
                }
                else
                {
                    SmartExecutor.execute(method, tempContext, beanContext.getBean(method.getDeclaringClass()));
                }
            }
        }
    }

    private Object getInstance(final AbstractBeanContext beanContext, final Class<?> clazz)
    {
        Object bean = null;
        try
        {
            bean = beanContext.getBean(clazz);
        }
        catch (final Exception e)
        {
            try
            {
                bean = clazz.newInstance();
            }
            catch (final InstantiationException | IllegalAccessException e1)
            {
                e1.printStackTrace();
            }
        }

        return bean;
    }

    private Collection<Class<?>> getPossibleAggregationPoints(final Class<?> clazz)
    {
        final Collection<Class<?>> possibilities = new HashSet<>();

        final Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class)
        {
            possibilities.add(superclass);
        }

        possibilities.addAll(Arrays.asList(clazz.getInterfaces()));

        return possibilities;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("listeners", this.listeners).toString();
    }
}
