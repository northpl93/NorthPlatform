package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import javassist.CtClass;
import javassist.CtConstructor;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class InjectorInstallScanningTask extends AbstractScanningTask
{
    private static final String INJECTOR_NAME = Injector.class.getName();

    public InjectorInstallScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
    }

    @Override
    boolean tryComplete()
    {
        final Collection<Method> postInjectMethods = this.postInjectMethods();

        final Field[] declaredFields;

        try
        {
            declaredFields = this.clazz.getDeclaredFields();
        }
        catch (final Throwable t)
        {
            return true;
        }

        for (final Field field : declaredFields)
        {
            if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(Inject.class))
            {
                try
                {
                    this.installInjector(postInjectMethods);
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            }
        }

        if (! postInjectMethods.isEmpty())
        {
            try
            {
                this.installInjector(postInjectMethods);
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
        }

        return true;
    }

    private Collection<Method> postInjectMethods()
    {
        try
        {
            return Arrays.stream(this.clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(PostInject.class)).collect(Collectors.toSet());
        }
        catch (final Throwable throwable)
        {
            return new HashSet<>();
        }
    }

    private void installInjector(final Collection<Method> postInject) throws Exception
    {
        final CtClass ctClass = this.ctClass;
        if (ctClass.isFrozen())
        {
            ctClass.defrost();
        }

        for (final CtConstructor ctConstructor : ctClass.getConstructors())
        {
            ctConstructor.insertAfter(INJECTOR_NAME + ".inject(this);");
            for (final Method method : postInject)
            {
                ctConstructor.insertAfter("this." + method.getName() + "();");
            }
        }

        API.getApiCore().getInstrumentationClient().redefineClass(ctClass.getName(), ctClass.toBytecode());
    }
}
