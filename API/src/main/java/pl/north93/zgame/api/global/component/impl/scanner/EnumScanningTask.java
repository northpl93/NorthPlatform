package pl.north93.zgame.api.global.component.impl.scanner;

import javassist.CtClass;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.injection.Injector;

class EnumScanningTask extends AbstractScanningTask
{
    public EnumScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
    }

    @Override
    boolean tryComplete()
    {
        final Object[] enumConstants;
        try
        {
            enumConstants = this.clazz.getEnumConstants();
        }
        catch (final NoClassDefFoundError error)
        {
            // nie ta platforma, pomijamy
            return true;
        }

        for (final Object enumConstant : enumConstants)
        {
            try
            {
                Injector.inject(enumConstant);
            }
            catch (final Exception e)
            {
                this.lastCause = e;
                return false;
            }
        }

        return true;
    }
}