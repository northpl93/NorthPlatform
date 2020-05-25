package pl.north93.northplatform.api.global.component.impl.general;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import lombok.ToString;

@ToString
public final class WeakClassPool
{
    private final WeakClassPool parent;
    private final List<LoaderClassPath> classPaths;
    private Reference<ClassPool> classPoolReference;

    public WeakClassPool(final WeakClassPool parent, final ClassLoader classLoader)
    {
        this.parent = parent;
        this.classPaths = new ArrayList<>();
        this.classPaths.add(new LoaderClassPath(classLoader));
        this.classPoolReference = new WeakReference<>(null);
    }

    public void addClassPath(final ClassLoader classLoader)
    {
        final LoaderClassPath loaderClassPath = new LoaderClassPath(classLoader);
        this.classPaths.add(loaderClassPath);

        final ClassPool cachedClassPool = this.classPoolReference.get();
        if (cachedClassPool == null)
        {
            return;
        }

        cachedClassPool.appendClassPath(loaderClassPath);
    }

    public ClassPool getClassPool()
    {
        final ClassPool cachedPool = this.classPoolReference.get();
        if (cachedPool != null)
        {
            return cachedPool;
        }

        final ClassPool classPool = this.instantiateClassPool();
        for (final LoaderClassPath classPath : this.classPaths)
        {
            classPool.appendClassPath(classPath);
        }

        this.classPoolReference = new WeakReference<>(classPool);
        return classPool;
    }

    private ClassPool instantiateClassPool()
    {
        if (this.parent != null)
        {
            final ClassPool parentClassPool = this.parent.getClassPool();
            return new ClassPool(parentClassPool);
        }

        return new ClassPool();
    }
}
