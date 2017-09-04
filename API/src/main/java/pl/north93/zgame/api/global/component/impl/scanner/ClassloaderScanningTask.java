package pl.north93.zgame.api.global.component.impl.scanner;

import static pl.north93.zgame.api.global.utils.JavaUtils.hideException;


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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javassist.ClassPool;
import javassist.CtClass;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.annotations.SkipInjections;
import pl.north93.zgame.api.global.component.impl.ComponentBundle;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;
import pl.north93.zgame.api.global.component.impl.JarComponentLoader;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;

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
        this.reflections = this.createReflections(new FilterBuilder().includePackage(rootPackage).includePackage("gui").includePackage("hotbar")); // todo allow to register own packages
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
            if (component.getPackages().isEmpty())
            {
                final int lastDot = component.getMainClass().lastIndexOf('.');
                filter.excludePackage(component.getMainClass().substring(0, lastDot));
            }
            else
            {
                for (final String pack : component.getPackages())
                {
                    filter.excludePackage(pack);
                }
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
        final Set<Pair<Class<?>, CtClass>> allClasses = this.getAllClasses(this.reflections, filter);
        for (final Pair<Class<?>, CtClass> entry : allClasses)
        {
            final Class<?> clazz = entry.getKey();
            final CtClass ctClass = entry.getValue();
            if (clazz.isAnnotationPresent(SkipInjections.class))
            {
                continue;
            }

            if (! clazz.isInterface() && ! clazz.isEnum())
            {
                // nie probujemy instalowac injectora w enumach i interfejsach
                // zeby nie marnowac czasu.
                this.injectorInstaller.tryInstall(ctClass);
            }

            final AbstractBeanContext beanContext = this.manager.getOwningContext(clazz);

            // dodajemy pozostale zadania do kolejki zeby wykonaly sie w miare mozliwosci
            this.pendingTasks.add(new StaticScanningTask(this, clazz, ctClass, beanContext));
            this.pendingTasks.add(new ConstructorScanningTask(this, clazz, ctClass, beanContext));
            this.pendingTasks.add(new MethodScanningTask(this, clazz, ctClass, beanContext));
            if (clazz.isEnum())
            {
                // dodatkowe wsparcie do wstrzykiwania wartosci w enumach
                this.pendingTasks.add(new EnumScanningTask(this, clazz, ctClass, beanContext));
            }
        }

        this.processQueue();
        if (! this.pendingTasks.isEmpty())
        {
            throw new RuntimeException(this.generateScanningError());
        }

        for (final Pair<Class<?>, CtClass> entry : allClasses)
        {
            final AbstractBeanContext beanContext = this.manager.getOwningContext(entry.getKey());
            this.manager.getAggregationManager().call(beanContext, entry.getValue(), entry.getKey());
        }
    }

    private void processQueue()
    {
        boolean modified = true;
        while (modified)
        {
            modified = false;
            final Iterator<AbstractScanningTask> iterator = this.pendingTasks.iterator();
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
    }

    private String generateScanningError()
    {
        final StrBuilder sb = new StrBuilder(512);
        sb.append("They're ").append(this.pendingTasks.size()).append(" uncompleted tasks! Below you will se trace of these tasks.").appendNewLine();
        for (final AbstractScanningTask task : this.pendingTasks)
        {
            sb.append("> > > TASK BEGIN > > >").appendNewLine();
            sb.append("|- Task type: ").append(task.getClass().getSimpleName()).appendNewLine();
            sb.append("|- Processed class: ").append(task.clazz.getName()).appendNewLine();
            sb.append("|- Bean context: ").append(task.beanContext.getBeanContextName()).appendNewLine();
            final Throwable lastCause = task.lastCause;
            if (lastCause != null)
            {
                sb.appendln("|- Last recorded exception: ");
                final String stack = StringUtils.leftPad(ExceptionUtils.getStackTrace(lastCause), 4);
                sb.appendln(stack);
            }
            sb.appendln("< < < TASK END < < <");
        }
        sb.append("End of uncompleted tasks list");
        return sb.toString();
    }

    /*default*/ Set<Pair<Class<?>, CtClass>> getAllClasses(final Reflections reflections, final FilterBuilder filter)
    {
        final Collection<String> classes = reflections.getStore().get(SubTypesScanner.class.getSimpleName()).values();
        final Set<Pair<Class<?>, CtClass>> out = new HashSet<>(classes.size());
        for (final String clazz : classes)
        {
            if (! filter.apply(clazz))
            {
                continue;
            }

            final Class<?> outClass = forName(clazz, reflections.getConfiguration().getClassLoaders());
            if (outClass != null)
            {
                out.add(Pair.of(outClass, hideException(() -> this.classPool.getCtClass(clazz))));
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

    public Reflections getReflections()
    {
        return this.reflections;
    }

    private Reflections createReflections(final FilterBuilder packageFilter)
    {
        final ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.setClassLoaders(new ClassLoader[]{this.classLoader});
        configuration.setScanners(new SubTypesScanner(false), new ResourcesScanner());
        configuration.setUrls(this.loadedFile);
        configuration.filterInputsBy(packageFilter);

        return new Reflections(configuration);
    }
}
