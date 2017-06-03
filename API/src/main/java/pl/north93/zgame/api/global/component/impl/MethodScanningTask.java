package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.DynamicBean;

class MethodScanningTask extends AbstractScanningTask
{
    private final Set<Method> methods;

    public MethodScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, beanContext);

        Set<Method> methods;
        try
        {
            methods = new HashSet<>(Arrays.asList(clazz.getDeclaredMethods()));
        }
        catch (final Throwable e)
        {
            methods = new HashSet<>();
        }
        this.methods = methods;
    }

    @Override
    boolean tryComplete()
    {
        final Iterator<Method> iterator = this.methods.iterator();
        while (iterator.hasNext())
        {
            final Method method = iterator.next();

            try
            {
                if (method.isAnnotationPresent(Bean.class))
                {
                    BeanFactory.INSTANCE.createStaticBean(this.beanContext, method);
                }
                else if (method.isAnnotationPresent(DynamicBean.class))
                {
                    BeanFactory.INSTANCE.createDynamicBean(this.beanContext, method);
                }
                else if (method.isAnnotationPresent(Aggregator.class))
                {
                    ComponentManagerImpl.instance.getAggregationManager().addAggregator(method);
                }
            }
            catch (final Exception ignored)
            {
                continue;
            }

            iterator.remove();
        }

        return this.methods.isEmpty();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("methods", this.methods).toString();
    }
}
