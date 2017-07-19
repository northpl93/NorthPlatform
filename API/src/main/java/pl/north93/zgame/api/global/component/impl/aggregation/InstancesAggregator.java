package pl.north93.zgame.api.global.component.impl.aggregation;

import static pl.north93.zgame.api.global.utils.JavaUtils.hideException;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.lazy.LazyValue;

import javassist.CtClass;
import pl.north93.zgame.api.global.component.impl.SmartExecutor;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.context.TemporaryBeanContext;

class InstancesAggregator implements IAggregator
{
    private final CtClass clazz;

    public InstancesAggregator(final CtClass clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public boolean isSuitableFor(final CtClass clazz)
    {
        if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface())
        {
            return false;
        }

        for (final CtClass ctClass : this.getPossibleAggregationPoints(clazz))
        {
            if (ctClass.getName().equals(this.clazz.getName()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void call(final AbstractBeanContext beanContext, final CtClass ctClass, final Class<?> javaClass, final LazyValue<Object> instance, final Method listener)
    {
        final TemporaryBeanContext tempContext = new TemporaryBeanContext(beanContext);
        tempContext.put(javaClass, instance.get());

        if (Modifier.isStatic(listener.getModifiers()))
        {
            SmartExecutor.execute(listener, tempContext, null);
        }
        else
        {
            SmartExecutor.execute(listener, tempContext, beanContext.getBean(listener.getDeclaringClass()));
        }
    }

    private Collection<CtClass> getPossibleAggregationPoints(final CtClass clazz)
    {
        final Collection<CtClass> possibilities = new HashSet<>();

        final CtClass superclass = hideException(clazz::getSuperclass);
        if (superclass != null && !superclass.getName().equals("java.lang.Object"))
        {
            possibilities.add(superclass);
        }

        possibilities.addAll(Arrays.asList(hideException(clazz::getInterfaces)));

        return possibilities;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final InstancesAggregator that = (InstancesAggregator) o;

        return this.clazz.equals(that.clazz);
    }

    @Override
    public int hashCode()
    {
        return this.clazz.hashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).toString();
    }
}
