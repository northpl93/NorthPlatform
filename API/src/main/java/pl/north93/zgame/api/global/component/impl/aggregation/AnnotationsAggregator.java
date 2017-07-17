package pl.north93.zgame.api.global.component.impl.aggregation;

import static pl.north93.zgame.api.global.component.impl.CtUtils.toJavaMethod;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.lazy.LazyValue;

import javassist.CtClass;
import javassist.CtMethod;
import pl.north93.zgame.api.global.component.impl.SmartExecutor;
import pl.north93.zgame.api.global.component.impl.container.BeanFactory;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.context.TemporaryBeanContext;

class AnnotationsAggregator implements IAggregator
{
    private final Class<? extends Annotation> annotation;

    public AnnotationsAggregator(final Class<? extends Annotation> annotation)
    {
        this.annotation = annotation;
    }

    @Override
    public boolean isSuitableFor(final CtClass clazz)
    {
        for (final CtMethod method : clazz.getDeclaredMethods())
        {
            if (method.hasAnnotation(this.annotation))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void call(final AbstractBeanContext beanContext, final CtClass clazz, final Class<?> javaClass, final LazyValue<Object> instance, final Method listener)
    {
        for (final CtMethod method : clazz.getDeclaredMethods())
        {
            if (! method.hasAnnotation(this.annotation))
            {
                continue;
            }
            // metoda ma sledzona przez nas adnotacja; wywolujemy listenera.
            this.callMethod(beanContext, instance, toJavaMethod(javaClass, method), listener);
        }
    }

    private void callMethod(final AbstractBeanContext beanContext, final LazyValue<Object> instance, final Method method, final Method listener)
    {
        final TemporaryBeanContext tempContext = new TemporaryBeanContext(beanContext);
        tempContext.put(this.annotation, method.getAnnotation(this.annotation));
        tempContext.put(Method.class, "Target", method);
        BeanFactory.INSTANCE.createLazyBean(tempContext, method.getDeclaringClass(), "MethodOwner", instance);

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
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("annotation", this.annotation).toString();
    }
}
