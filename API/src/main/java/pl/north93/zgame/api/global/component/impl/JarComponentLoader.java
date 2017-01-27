package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import pl.north93.zgame.api.global.component.IComponentManager;

class JarComponentLoader extends URLClassLoader
{
    private final URL                     fileUrl;
    private final Set<String>             scannedPackages;
    private final Map<String, Class<?>>   classCache;
    private final Set<JarComponentLoader> dependencies;

    public JarComponentLoader(final URL url, final ClassLoader parent)
    {
        super(new URL[] { url }, parent);
        this.fileUrl = url;
        this.scannedPackages = new ObjectArraySet<>(4);
        this.classCache = new ConcurrentHashMap<>(16);
        this.dependencies = new HashSet<>();
    }

    @Override
    public URL getResource(final String name) // modified to search resources only in this jar
    {
        return this.findResource(name);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException
    {
        final Class<?> fromCache = this.classCache.get(name);
        if (fromCache != null)
        {
            return fromCache;
        }

        try
        {
            final Class<?> clazz = super.findClass(name);
            this.classCache.put(name, clazz);
            return clazz;
        }
        catch (final ClassNotFoundException ignored)
        {
        }

        for (final JarComponentLoader dependency : this.dependencies)
        {
            final Class<?> fromDependency = dependency.findClass(name);
            if (fromDependency != null)
            {
                this.classCache.put(name, fromDependency);
                return fromDependency;
            }
        }

        throw new ClassNotFoundException(name);
    }

    public URL getFileUrl()
    {
        return this.fileUrl;
    }

    public void scan(final IComponentManager componentManager, final Set<String> packagesToScan)
    {
        final Sets.SetView<String> toScan = Sets.difference(packagesToScan, this.scannedPackages);

        if (toScan.isEmpty())
        {
            return;
        }

        ClassScanner.scan(this.fileUrl, this, componentManager, toScan);
        this.scannedPackages.addAll(toScan);
    }

    public void registerDependency(final JarComponentLoader loader)
    {
        if (loader == this)
        {
            return;
        }
        this.dependencies.add(loader);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fileUrl", this.fileUrl).toString();
    }

    static
    {
        try
        {
            final Method ex = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable");
            if (ex != null)
            {
                ex.setAccessible(true);
                ex.invoke(null);
            }
        }
        catch (final Exception var3)
        {
            System.err.println("Failed to set JarComponentLoader as parallel capable");
        }
    }
}
