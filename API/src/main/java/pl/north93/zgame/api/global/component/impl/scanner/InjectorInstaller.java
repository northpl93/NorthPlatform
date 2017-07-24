package pl.north93.zgame.api.global.component.impl.scanner;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.impl.injection.Injector;

class InjectorInstaller
{
    private static final String INJECTOR_NAME = Injector.class.getName();

    void tryInstall(final CtClass ctClass)
    {
        final Collection<CtMethod> postInjectMethods = this.postInjectMethods(ctClass);

        for (final CtField field : ctClass.getDeclaredFields())
        {
            if (!Modifier.isStatic(field.getModifiers()) && field.hasAnnotation(Inject.class))
            {
                try
                {
                    this.installInjector(ctClass, postInjectMethods);
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
                return;
            }
        }

        if (! postInjectMethods.isEmpty())
        {
            try
            {
                this.installInjector(ctClass, postInjectMethods);
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private Collection<CtMethod> postInjectMethods(final CtClass clazz)
    {
        try
        {
            return Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.hasAnnotation(PostInject.class)).collect(Collectors.toSet());
        }
        catch (final Throwable throwable)
        {
            return new HashSet<>();
        }
    }

    private void installInjector(final CtClass ctClass, final Collection<CtMethod> postInject) throws Exception
    {
        if (ctClass.isFrozen())
        {
            ctClass.defrost();
        }

        for (final CtConstructor ctConstructor : ctClass.getDeclaredConstructors())
        {
            ctConstructor.insertAfter(INJECTOR_NAME + ".inject(this);");
            for (final CtMethod method : postInject)
            {
                ctConstructor.insertAfter("this." + method.getName() + "();");
            }
        }

        API.getApiCore().getInstrumentationClient().redefineClass(ctClass.getName(), ctClass.toBytecode());
    }
}
