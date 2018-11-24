package pl.north93.northplatform.api.global.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.utils.lang.CatchException;
import pl.rafsze.utils.agent.InstrumentationFactory;

public class InstrumentationClient
{
    private final Instrumentation instrumentation = InstrumentationFactory.getInstrumentation();

    public void redefineClass(final String className, final byte[] newBytes)
    {
        try
        {
            Class<?> targetClazz = Class.forName(className);
            CatchException.sneaky(() -> instrumentation.redefineClasses(new ClassDefinition(targetClazz, newBytes)));
            return;
        }
        catch (final ClassNotFoundException ignored)
        {
        }
        
        // now try the hard/slow way
        for (final Class<?> clazz: this.instrumentation.getAllLoadedClasses())
        {
            if ( clazz.getName().equals(className) )
            {
                CatchException.sneaky(() -> this.instrumentation.redefineClasses(new ClassDefinition(clazz, newBytes)));
                return;
            }
        }
        
        throw new RuntimeException("Failed to locate class [" + className + "]");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("instrumentation", this.instrumentation).toString();
    }
}