package pl.north93.northplatform.api.global.component.impl.general;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.IComponentBundle;

// klasa pomocnicza skanująca najpierw główny classloader, a później komponenty.
/*default*/ class ClassResolver
{
    private final ComponentManagerImpl  componentManager;
    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    public ClassResolver(final ComponentManagerImpl componentManager)
    {
        this.componentManager = componentManager;
    }

    public Class<?> findClass(final String name)
    {
        return this.classCache.computeIfAbsent(name, this::doFindClass);
    }

    private Class<?> doFindClass(final String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (final ClassNotFoundException e)
        {
            for (final IComponentBundle iComponentBundle : this.componentManager.getComponents())
            {
                try
                {
                    return Class.forName(name, true, iComponentBundle.getClassLoader());
                }
                catch (final ClassNotFoundException ignored)
                {
                }
            }
        }

        throw new RuntimeException("I can't find class " + name);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("classCache", this.classCache).toString();
    }
}
