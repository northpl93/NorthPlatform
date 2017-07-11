package pl.north93.zgame.api.global.component.impl.scanner;

import static pl.north93.zgame.api.global.component.impl.CtUtils.toJavaConstructor;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import javassist.CtConstructor;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.exceptions.BeanCreationException;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.container.BeanFactory;

class ConstructorScanningTask extends AbstractScanningTask
{
    private final Set<CtConstructor> constructors;

    public ConstructorScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
        this.constructors = new HashSet<>(Arrays.asList(this.ctClass.getDeclaredConstructors()));
    }

    @Override
    boolean tryComplete()
    {
        final Iterator<CtConstructor> iterator = this.constructors.iterator();
        while (iterator.hasNext())
        {
            final CtConstructor constructor = iterator.next();
            try
            {
                if (constructor.hasAnnotation(Bean.class))
                {
                    BeanFactory.INSTANCE.createStaticBean(this.beanContext, toJavaConstructor(this.clazz, constructor));
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
        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("constructors", this.constructors).toString();
    }
}
