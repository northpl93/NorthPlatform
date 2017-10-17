package pl.north93.zgame.api.global.component.impl.aggregation;

import static pl.north93.zgame.api.global.utils.lang.JavaUtils.hideException;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.lazy.LazyValue;

import javassist.CtClass;
import pl.north93.zgame.api.global.component.impl.general.SmartExecutor;
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
        return ! Modifier.isAbstract(clazz.getModifiers()) && ! clazz.isInterface() && this.isSuitable(clazz);
    }

    private boolean isSuitable(final CtClass checkedClass)
    {
        if (this.clazz.isInterface())
        {
            final CtClass[] interfaces = hideException(checkedClass::getInterfaces);
            if (interfaces != null)
            {
                for (final CtClass anInterface : interfaces)
                {
                    if (anInterface.getName().equals(this.clazz.getName()))
                    {
                        return true;
                    }
                }
            }
        }

        final CtClass superclass = hideException(checkedClass::getSuperclass);
        if (superclass != null && !superclass.getName().equals("java.lang.Object"))
        {
            return superclass.getName().equals(this.clazz.getName()) || this.isSuitable(superclass);
        }

        // nic nie pasuje
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
