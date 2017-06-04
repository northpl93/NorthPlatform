package pl.north93.zgame.api.global.component.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import javassist.NotFoundException;

abstract class AbstractScanningTask
{
    protected final ClassloaderScanningTask classloaderScanner;
    protected final Class<?>                clazz;
    protected final AbstractBeanContext     beanContext;

    public AbstractScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final AbstractBeanContext beanContext)
    {
        this.classloaderScanner = classloaderScanner;
        this.clazz = clazz;
        this.beanContext = beanContext;
    }

    /*default*/ abstract boolean tryComplete();

    protected final CtClass getCtClass()
    {
        try
        {
            return this.classloaderScanner.getClassPool().getCtClass(this.clazz.getName());
        }
        catch (final NotFoundException e)
        {
            throw new RuntimeException("Failed to convert class " + this.clazz.getName() + " to CtClass", e);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).append("beanContext", this.beanContext).toString();
    }
}
