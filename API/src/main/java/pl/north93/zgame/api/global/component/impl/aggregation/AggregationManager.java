package pl.north93.zgame.api.global.component.impl.aggregation;

import static pl.north93.zgame.api.global.utils.lang.JavaUtils.hideException;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.lazy.LazyValue;

import javassist.ClassPool;
import javassist.CtClass;
import pl.north93.zgame.api.global.component.annotations.SkipInjections;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;
import pl.north93.zgame.api.global.component.impl.SmartExecutor;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;

public class AggregationManager
{
    private Multimap<IAggregator, Method> listeners = ArrayListMultimap.create();

    public void addAggregator(final Method method)
    {
        final Aggregator aggregatorAnn = method.getAnnotation(Aggregator.class);
        final Class<?> clazz = aggregatorAnn.value();

        if (clazz.isAnnotation())
        {
            //noinspection unchecked
            this.listeners.put(new AnnotationsAggregator((Class<? extends Annotation>) clazz), method);
        }
        else
        {
            final ClassPool classPool = ComponentManagerImpl.instance.getClassPool(method.getDeclaringClass().getClassLoader());
            final CtClass ctClass = hideException(() -> classPool.getCtClass(clazz.getName()));

            this.listeners.put(new InstancesAggregator(ctClass), method);
        }
    }

    public void call(final AbstractBeanContext beanContext, final CtClass ctClass, final Class<?> javaClass)
    {
        if (ctClass.hasAnnotation(SkipInjections.class))
        {
            return;
        }

        final LazyValue<Object> object = new LazyValue<>(() -> this.getInstance(beanContext, javaClass));

        for (final IAggregator aggregator : this.listeners.keySet())
        {
            if (! aggregator.isSuitableFor(ctClass))
            {
                continue;
            }

            final Collection<Method> listeners = this.listeners.get(aggregator);
            for (final Method listener : listeners)
            {
                aggregator.call(beanContext, ctClass, javaClass, object,listener);
            }
        }
    }

    private Object getInstance(final AbstractBeanContext beanContext, final Class<?> clazz)
    {
        try
        {
            return beanContext.getBean(clazz);
        }
        catch (final Exception e)
        {
            final Constructor<?> firstConstructor = clazz.getDeclaredConstructors()[0];
            return SmartExecutor.execute(firstConstructor, beanContext, null);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("listeners", this.listeners).toString();
    }
}
