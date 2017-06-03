package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javassist.CtClass;
import javassist.CtConstructor;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class InjectorInstallScanningTask extends AbstractScanningTask
{
    private static final String INJECTOR_NAME = Injector.class.getName();

    public InjectorInstallScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, beanContext);
    }

    @Override
    boolean tryComplete()
    {
        for (final Field field : this.clazz.getDeclaredFields())
        {
            if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(Inject.class))
            {
                try
                {
                    this.installInjector();
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
        return true;
    }

    private void installInjector() throws Exception
    {
        final CtClass ctClass = this.classloaderScanner.getClassPool().get(this.clazz.getName());

        for (final CtConstructor ctConstructor : ctClass.getConstructors())
        {
            ctConstructor.insertAfter(INJECTOR_NAME + ".inject(this);");
        }

        API.getApiCore().getInstrumentationClient().redefineClass(ctClass.getName(), ctClass.toBytecode());
    }
}
