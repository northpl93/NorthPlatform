package pl.north93.zgame.api.global.component.impl.scanner;

import static pl.north93.zgame.api.global.component.impl.CtUtils.toJavaMethod;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import javassist.CtMethod;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.DynamicBean;
import pl.north93.zgame.api.global.component.exceptions.BeanCreationException;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.container.BeanFactory;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;

class MethodScanningTask extends AbstractScanningTask
{
    private final Set<CtMethod> methods;

    public MethodScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
        this.methods = new HashSet<>(Arrays.asList(this.ctClass.getDeclaredMethods()));
    }

    @Override
    boolean tryComplete()
    {
        final Iterator<CtMethod> iterator = this.methods.iterator();
        while (iterator.hasNext())
        {
            final CtMethod method = iterator.next();
            try
            {
                if (method.hasAnnotation(Bean.class))
                {
                    BeanFactory.INSTANCE.createStaticBean(this.beanContext, toJavaMethod(this.clazz, method));
                }
                else if (method.hasAnnotation(DynamicBean.class))
                {
                    BeanFactory.INSTANCE.createDynamicBean(this.beanContext, toJavaMethod(this.clazz, method));
                }
                else if (method.hasAnnotation(Aggregator.class))
                {
                    ComponentManagerImpl.instance.getAggregationManager().addAggregator(toJavaMethod(this.clazz, method));
                }
            }
            catch (final BeanCreationException ex)
            {
                ex.printStackTrace();
                continue;
            }
            catch (final Exception exception)
            {
                this.lastCause = exception;
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
