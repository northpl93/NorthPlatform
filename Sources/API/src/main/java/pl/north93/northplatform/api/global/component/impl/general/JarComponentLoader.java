package pl.north93.northplatform.api.global.component.impl.general;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import lombok.ToString;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.impl.context.JarBeanContext;
import pl.north93.northplatform.api.global.component.impl.context.RootBeanContext;
import pl.north93.northplatform.api.global.component.impl.scanner.ClassloaderScanningTask;

@ToString(of = {"fileUrl"})
public class JarComponentLoader extends URLClassLoader
{
    private final URL fileUrl;
    private final Set<JarComponentLoader> dependencies;
    private final JarBeanContext beanContext;
    private final WeakClassPool weakClassPool;
    private ClassloaderScanningTask scanningTask;

    public JarComponentLoader(final RootBeanContext rootBeanContext, final URL url, final ClassLoader parent)
    {
        super(new URL[] { url }, parent);
        this.fileUrl = url;
        this.dependencies = new HashSet<>();
        this.beanContext = new JarBeanContext(rootBeanContext, this);

        final WeakClassPool apiPool = ComponentManagerImpl.instance.getWeakClassPool(ApiCore.class.getClassLoader());
        this.weakClassPool = new WeakClassPool(apiPool, this);
    }

    @Override
    public URL getResource(final String name)
    {
        if (name.endsWith(".class"))
        {
            return super.getResource(name);
        }
        
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
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException
    {
        try
        {
            return super.loadClass(name, resolve);
        }
        catch (final ClassNotFoundException ignored)
        {
        }

        for (final JarComponentLoader dependency : this.dependencies)
        {
            try
            {
                return dependency.loadClass(name, resolve);
            }
            catch (final ClassNotFoundException ignored) // class not found in this dependency
            {
            }
        }

        throw new ClassNotFoundException(name);
    }

    public Class<?> findClassWithoutDependencies(final String name) throws ClassNotFoundException
    {
        return super.loadClass(name, true);
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
        this.weakClassPool.addClassPath(loader);
    }

    public ClassloaderScanningTask getScanningTask()
    {
        return this.scanningTask;
    }

    public void setScanningTask(final ClassloaderScanningTask scanningTask)
    {
        this.scanningTask = scanningTask;
    }

    public WeakClassPool getWeakClassPool()
    {
        return this.weakClassPool;
    }

    public JarBeanContext getBeanContext()
    {
        return this.beanContext;
    }

    static
    {
        registerAsParallelCapable();
    }
}
