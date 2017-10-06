package pl.north93.zgame.api.global.component.impl.scanner;

import java.lang.reflect.Modifier;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.impl.injection.Injector;

class InjectorInstaller
{
    private static final String INJECTOR_NAME = Injector.class.getName();

    void tryInstall(final CtClass ctClass)
    {
        for (final CtField field : ctClass.getDeclaredFields())
        {
            if (!Modifier.isStatic(field.getModifiers()) && field.hasAnnotation(Inject.class))
            {
                try
                {
                    this.installInjector(ctClass);
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    private void installInjector(final CtClass ctClass) throws Exception
    {
        if (ctClass.isFrozen())
        {
            ctClass.defrost();
        }

        for (final CtConstructor ctConstructor : ctClass.getDeclaredConstructors())
        {
            // na poczatek kod wstrzykujacy
            ctConstructor.insertBeforeBody(INJECTOR_NAME + ".inject(this, " + ctClass.getName() + ".class);");
        }

        API.getApiCore().getInstrumentationClient().redefineClass(ctClass.getName(), ctClass.toBytecode());
    }
}
