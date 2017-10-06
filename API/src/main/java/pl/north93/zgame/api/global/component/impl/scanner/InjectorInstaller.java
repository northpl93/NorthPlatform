package pl.north93.zgame.api.global.component.impl.scanner;

import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.impl.injection.Injector;

class InjectorInstaller
{
    private static final String INJECTOR_NAME = Injector.class.getName();
    private final ApiCore apiCore;

    InjectorInstaller(final ApiCore apiCore)
    {
        this.apiCore = apiCore;
    }

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
                    final LogRecord logRecord = new LogRecord(Level.SEVERE, "Failed to install injector in {0}");
                    logRecord.setParameters(new Object[] {ctClass.getName()});
                    logRecord.setThrown(e);

                    this.apiCore.getLogger().log(logRecord);
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

        this.apiCore.getInstrumentationClient().redefineClass(ctClass.getName(), ctClass.toBytecode());

        this.apiCore.getLogger().log(Level.FINE, "Installed injector in {0}", ctClass.getName());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
