package pl.north93.zgame.api.global.component.impl.scanner;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;
import pl.north93.zgame.api.global.component.annotations.SkipInjections;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.ComponentBundle;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;
import pl.north93.zgame.api.global.component.impl.JarComponentLoader;

public class ClassloaderScanningTask
{
    private final ComponentManagerImpl        manager;
    private final ClassLoader                 classLoader;
    private final URL                         loadedFile;
    private final ClassPool                   classPool;
    private final Reflections                 reflections;
    private final InjectorInstaller           injectorInstaller;
    private final Queue<AbstractScanningTask> pendingTasks;

    public ClassloaderScanningTask(final ComponentManagerImpl manager, final ClassLoader classLoader, final URL loadedFile, final String rootPackage)
    {
        this.manager = manager;
        this.classLoader = classLoader;
        this.loadedFile = loadedFile;
        this.classPool = manager.getClassPool(classLoader);
        this.injectorInstaller = new InjectorInstaller();
        this.reflections = this.createReflections(new FilterBuilder().includePackage(rootPackage));
        this.pendingTasks = new ArrayDeque<>();
    }

    public static ClassloaderScanningTask create(final ComponentManagerImpl manager, final ClassLoader classLoader, final String rootPackage)
    {
        Preconditions.checkNotNull(manager, "Manager can't be null!");
        Preconditions.checkNotNull(classLoader, "ClassLoader can't be null");
        Preconditions.checkNotNull(rootPackage, "Root package can't be null!");

        if (classLoader instanceof JarComponentLoader)
        {
            return new ClassloaderScanningTask(manager, classLoader, ((JarComponentLoader) classLoader).getFileUrl(), rootPackage);
        }
        else if (classLoader == ClassloaderScanningTask.class.getClassLoader())
        {
            return new ClassloaderScanningTask(manager, classLoader, ClassloaderScanningTask.class.getProtectionDomain().getCodeSource().getLocation(), rootPackage);
        }
        throw new IllegalArgumentException();
    }

    public void scanWithoutComponents(final List<ComponentDescription> components)
    {
        final FilterBuilder filter = new FilterBuilder();
        for (final ComponentDescription component : components)
        {
            final String pack;
            if (StringUtils.isEmpty(component.getPackageToScan()))
            {
                final int lastDot = component.getMainClass().lastIndexOf('.');
                pack = component.getMainClass().substring(0, lastDot);
            }
            else
            {
                pack = component.getPackageToScan();
            }
            filter.excludePackage(pack);

            try
            {
                final Class<?> aClass = Class.forName(component.getMainClass(), true, this.classLoader);
                final IncludeInScanning annotation = aClass.getAnnotation(IncludeInScanning.class);
                if (annotation != null)
                {
                    filter.excludePackage(annotation.value());
                }
            }
            catch (final ClassNotFoundException | NoClassDefFoundError ignored) // jak sie nie uda zaladowac klasy to trudno, i tak jej nie uzyjemy
            {
            }
        }
        this.scan(filter);
    }

    public void scanComponent(final ComponentBundle bundle)
    {
        final FilterBuilder filter = new FilterBuilder();
        for (final String pack : bundle.getBasePackages())
        {
            filter.includePackage(pack);
        }
        this.scan(filter);
    }

    private void scan(final FilterBuilder filter)
    {
        final Set<Class<?>> allClasses = this.getAllClasses(this.reflections, filter);
        for (final Class<?> aClass : allClasses)
        {
            if (aClass.isAnnotationPresent(SkipInjections.class))
            {
                continue;
            }

            final AbstractBeanContext beanContext = this.manager.getOwningContext(aClass);
            final CtClass ctClass;
            try
            {
                ctClass = this.classPool.get(aClass.getName());
            }
            catch (final NotFoundException e)
            {
                e.printStackTrace();
                continue;
            }

            this.injectorInstaller.tryInstall(ctClass);

            // dodajemy pozostale zadania do kolejki zeby wykonaly sie w miare mozliwosci
            this.pendingTasks.add(new StaticScanningTask(this, aClass, ctClass, beanContext));
            this.pendingTasks.add(new ConstructorScanningTask(this, aClass, ctClass, beanContext));
            this.pendingTasks.add(new MethodScanningTask(this, aClass, ctClass, beanContext));
        }

        final Iterator<AbstractScanningTask> iterator = this.pendingTasks.iterator();
        boolean modified = true;
        while (modified)
        {
            modified = false;
            while (iterator.hasNext())
            {
                final AbstractScanningTask task = iterator.next();
                if (task.tryComplete())
                {
                    modified = true;
                    iterator.remove();
                }
            }
        }

        if (! this.pendingTasks.isEmpty())
        {
            throw new RuntimeException("There're uncompletable class processing tasks."); // todo other exception
        }

        for (final Class<?> aClass : allClasses)
        {
            final AbstractBeanContext beanContext = this.manager.getOwningContext(aClass);
            this.manager.getAggregationManager().call(beanContext, aClass);
        }
    }

    /*default*/ Set<Class<?>> getAllClasses(final Reflections reflections, final FilterBuilder filter)
    {
        final Collection<String> classes = reflections.getStore().get(SubTypesScanner.class.getSimpleName()).values();
        final Set<Class<?>> out = new HashSet<>(classes.size());
        for (final String clazz : classes)
        {
            if (! filter.apply(clazz))
            {
                continue;
            }

            final Class<?> outClass = forName(clazz, reflections.getConfiguration().getClassLoaders());
            if (outClass != null)
            {
                out.add(outClass);
            }
        }
        return out;
    }

    private static Class<?> forName(final String type, final ClassLoader... classLoaders)
    {
        for (final ClassLoader classLoader : classLoaders)
        {
            try
            {
                // wymusza zainicjowanie klasy
                // jak sie nie uda to leci wyjatek ktory ignorujemy
                return Class.forName(type, true, classLoader);
            }
            catch (final Throwable ignored)
            {
            }
        }

        return null;
    }

    private Reflections createReflections(final FilterBuilder packageFilter)
    {
        final ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.setClassLoaders(new ClassLoader[]{this.classLoader});
        configuration.setScanners(new SubTypesScanner(false));
        configuration.setUrls(this.loadedFile);
        configuration.filterInputsBy(packageFilter);

        return new Reflections(configuration);
    }
}
