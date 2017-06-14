package pl.north93.zgame.api.global.component.impl.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.impl.SmartExecutor;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.context.TemporaryBeanContext;

class DynamicBeanContainer extends AbstractBeanContainer
{
    private final AbstractBeanContext beanContext;
    private final Method              method;

    public DynamicBeanContainer(final Class<?> type, final String name, final AbstractBeanContext beanContext, final Method method)
    {
        super(type, name);
        this.beanContext = beanContext;
        this.method = method;
    }

    @Override
    public Object getValue(final AccessibleObject injectionContext)
    {
        final TemporaryBeanContext beanContext = new TemporaryBeanContext(this.beanContext);

        beanContext.put(Class.class, "Source", ((Member) injectionContext).getDeclaringClass());
        for (final Annotation annotation : injectionContext.getAnnotations())
        {
            beanContext.put(annotation.getClass(), annotation);
        }

        if (Modifier.isStatic(this.method.getModifiers()))
        {
            return SmartExecutor.execute(this.method, beanContext, null);
        }
        else
        {
            return SmartExecutor.execute(this.method, beanContext, this.beanContext.getBean(this.method.getDeclaringClass()));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("beanContext", this.beanContext).append("method", this.method).toString();
    }
}
