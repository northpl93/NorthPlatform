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
    protected       Throwable               lastCause;

    public AbstractScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        this.classloaderScanner = classloaderScanner;
        this.clazz = clazz;
        this.beanContext = beanContext;
        this.ctClass = ctClass;
    }

    public final boolean tryComplete()
    {
        if (this.shouldSkipClass())
        {
            return true;
        }

        return this.tryComplete0();
    }

    /*default*/ abstract boolean tryComplete0();

    public final Throwable getLastCause()
    {
        return this.lastCause;
    }

    private boolean shouldSkipClass()
    {
        try
        {
            final boolean active = this.classloaderScanner.getManager().getProfileManager().isActive(this.clazz);
            if (! active)
            {
                return true;
            }
        }
        catch (final Exception e)
        {
            this.lastCause = e;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).append("beanContext", this.beanContext).toString();
    }
}
