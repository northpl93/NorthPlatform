package pl.north93.zgame.api.global.component.impl.scanner;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;

abstract class AbstractScanningTask
{
    protected final ClassloaderScanningTask classloaderScanner;
    protected final Class<?>                clazz;
    protected final AbstractBeanContext     beanContext;
    protected final CtClass                 ctClass;

    public AbstractScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        this.classloaderScanner = classloaderScanner;
        this.clazz = clazz;
        this.beanContext = beanContext;
        this.ctClass = ctClass;
    }

    /*default*/ abstract boolean tryComplete();

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).append("beanContext", this.beanContext).toString();
    }
}
