package pl.north93.zgame.api.global.agent.host;

import java.lang.instrument.Instrumentation;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AgentService implements AgentServiceMBean
{
    private final Instrumentation instrumentation;

    public AgentService(final Instrumentation instrumentation)
    {
        this.instrumentation = instrumentation;
    }

    @Override
    public void transformClass(final String className, final byte[] newBytes)
    {
        Class<?> targetClazz;
        ClassLoader targetClassLoader;
        // first see if we can locate the class through normal means
        try
        {
            targetClazz = Class.forName(className);
            targetClassLoader = targetClazz.getClassLoader();
            this.transform(targetClazz, targetClassLoader, newBytes);
            return;
        }
        catch (final Exception ignored)
        {
        }
        // now try the hard/slow way
        for (final Class<?> clazz: this.instrumentation.getAllLoadedClasses())
        {
            if (clazz.getName().equals(className))
            {
                targetClazz = clazz;
                targetClassLoader = targetClazz.getClassLoader();
                this.transform(targetClazz, targetClassLoader, newBytes);
                return;
            }
        }
        throw new RuntimeException("Failed to locate class [" + className + "]");
    }

    /**
     * Registers a transformer and executes the transform
     * @param clazz The class to transform
     * @param classLoader The classloader the class was loaded from
     */
    protected void transform(final Class<?> clazz, final ClassLoader classLoader, final byte[] newBytes)
    {
        final NorthTransformer transformer = new NorthTransformer(clazz.getName(), classLoader, newBytes);
        this.instrumentation.addTransformer(transformer, true);
        try
        {
            this.instrumentation.retransformClasses(clazz);
        }
        catch (final Exception ex)
        {
            throw new RuntimeException("Failed to transform [" + clazz.getName() + "]", ex);
        }
        finally
        {
            this.instrumentation.removeTransformer(transformer);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("instrumentation", this.instrumentation).toString();
    }
}
