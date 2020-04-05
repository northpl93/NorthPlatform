package pl.north93.northplatform.api.global.component.impl.general;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

// klasa pomocnicza skanująca najpierw główny classloader, a później komponenty.
/*default*/ class ClassResolver
{
    private final ComponentManagerImpl componentManager;
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
        catch (final ClassNotFoundException ignored)
        {
        }

        for (final JarComponentLoader classLoader : this.getClassLoaders())
        {
            try
            {
                return classLoader.findClassWithoutDependencies(name);
            }
            catch (final ClassNotFoundException ignored)
            {
            }
        }

        throw new RuntimeException("I can't find class " + name);
    }

    private Set<JarComponentLoader> getClassLoaders()
    {
        return this.componentManager.getComponents().stream().map(component ->
        {
            if (component.getClassLoader() instanceof JarComponentLoader)
            {
                return (JarComponentLoader) component.getClassLoader();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("classCache", this.classCache).toString();
    }
}
