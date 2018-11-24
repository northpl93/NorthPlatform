package pl.north93.northplatform.api.global.component.impl.general;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import pl.north93.northplatform.api.global.component.impl.scanner.ClassloaderScanningTask;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.impl.context.JarBeanContext;
import pl.north93.northplatform.api.global.component.impl.context.RootBeanContext;

public class JarComponentLoader extends URLClassLoader
{
    private final URL                     fileUrl;
    private final Map<String, Class<?>>   classCache;
    private final Set<JarComponentLoader> dependencies;
    private final JarBeanContext          beanContext;
    private final ClassPool               classPool;
    private       ClassloaderScanningTask scanningTask;

    public JarComponentLoader(final RootBeanContext rootBeanContext, final URL url, final ClassLoader parent)
    {
        super(new URL[] { url }, parent);
        this.fileUrl = url;
        this.classCache = new ConcurrentHashMap<>(16);
        this.dependencies = new HashSet<>();
        this.beanContext = new JarBeanContext(rootBeanContext, this);

        final ClassPool apiPool = ComponentManagerImpl.instance.getClassPool(ApiCore.class.getClassLoader());
        this.classPool = new ClassPool(apiPool);
        this.classPool.appendClassPath(new LoaderClassPath(this));
    }

    @Override
    public URL getResource(final String name)
    {
        URL resource = this.findResource(name);
        if (resource != null)
        {
            return resource;
        }

        // u nas nie ma, sprawdzamy w naszych zaleznosciach
        for (final JarComponentLoader dependency : this.dependencies)
        {
            if ((resource = dependency.getResource(name)) != null)
            {
                // udalo sie znalezc w ClassLoaderze zaleznosci
                return resource;
            }
        }

        return null;
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
            try
            {
                final Class<?> fromDependency = dependency.findClass(name);
                this.classCache.put(name, fromDependency);
                return fromDependency;
            }
            catch (final ClassNotFoundException ignored) // class not found in this dependency
            {
            }
        }

        throw new ClassNotFoundException(name);
    }

    public URL getFileUrl()
    {
        return this.fileUrl;
    }

    public void registerDependency(final JarComponentLoader loader)
    {
        if (loader == this)
        {
            return;
        }
        this.dependencies.add(loader);
        this.classPool.appendClassPath(new LoaderClassPath(loader));
    }

    public ClassloaderScanningTask getScanningTask()
    {
        return this.scanningTask;
    }

    public void setScanningTask(final ClassloaderScanningTask scanningTask)
    {
        this.scanningTask = scanningTask;
    }

    public ClassPool getClassPool()
    {
        return this.classPool;
    }

    public Set<JarComponentLoader> getDependencies()
    {
        return this.dependencies;
    }

    public JarBeanContext getBeanContext()
    {
        return this.beanContext;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fileUrl", this.fileUrl).toString();
    }

    static
    {
        registerAsParallelCapable();
    }
}
