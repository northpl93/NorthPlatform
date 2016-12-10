package pl.north93.zgame.api.global.agent.host;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NorthTransformer implements ClassFileTransformer
{
    private final String      name;
    private final ClassLoader classLoader;
    private final byte[]      bytes;

    public NorthTransformer(final String name, final ClassLoader classLoader, final byte[] bytes)
    {
        this.name = name;
        this.classLoader = classLoader;
        this.bytes = bytes;
    }

    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException
    {
        if (this.classLoader.equals(loader) && this.name.equals(className))
        {
            return this.bytes;
        }
        return classfileBuffer;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("classLoader", this.classLoader).append("bytes", this.bytes).toString();
    }
}
