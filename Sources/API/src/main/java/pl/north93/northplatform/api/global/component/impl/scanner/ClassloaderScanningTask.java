package pl.north93.northplatform.api.global.component.impl.scanner;

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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.TextStringBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import lombok.ToString;
import pl.north93.northplatform.api.global.component.ComponentDescription;
import pl.north93.northplatform.api.global.component.annotations.SkipInjections;
import pl.north93.northplatform.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.northplatform.api.global.component.impl.general.ComponentBundle;
import pl.north93.northplatform.api.global.component.impl.general.ComponentManagerImpl;
import pl.north93.northplatform.api.global.component.impl.general.JarComponentLoader;
import pl.north93.northplatform.api.global.component.impl.general.WeakClassPool;

@ToString(of = {"classLoader"})
public class ClassloaderScanningTask
{
    private final ComponentManagerImpl manager;
    private final ClassLoader classLoader;
    private final URL loadedFile;
    private final WeakClassPool weakClassPool;
    private final Reflections reflections;
    private final InjectorInstaller injectorInstaller;

    public ClassloaderScanningTask(final ComponentManagerImpl manager, final ClassLoader classLoader, final URL loadedFile, final Set<String> excludedPackages)
    {
        this.manager = manager;
        this.classLoader = classLoader;
        this.loadedFile = loadedFile;
        this.weakClassPool = manager.getWeakClassPool(classLoader);
        this.injectorInstaller = new InjectorInstaller(manager.getInstrumentationClient());

        final FilterBuilder filterBuilder = new FilterBuilder();
        for (final String excludedPackage : excludedPackages)
        {
            filterBuilder.excludePackage(excludedPackage);
        }
        this.reflections = this.createReflections(filterBuilder);
    }

    public static ClassloaderScanningTask create(final ComponentManagerImpl manager, final ClassLoader classLoader, final Set<String> excludedPackages)
    {
        Preconditions.checkNotNull(manager, "Manager can't be null!");
        Preconditions.checkNotNull(classLoader, "ClassLoader can't be null");
        Preconditions.checkNotNull(excludedPackages, "ExcludedPackages can't be null!");

        if (classLoader instanceof JarComponentLoader)
        {
            return new ClassloaderScanningTask(manager, classLoader, ((JarComponentLoader) classLoader).getFileUrl(), excludedPackages);
        }
        else if (classLoader == ClassloaderScanningTask.class.getClassLoader())
        {
            return new ClassloaderScanningTask(manager, classLoader, ClassloaderScanningTask.class.getProtectionDomain().getCodeSource().getLocation(), excludedPackages);
        }
        throw new IllegalArgumentException();
    }

    // first stage of scanning, done before loading components
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

    // second stage of scanning, done when component is being loaded
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
        // accumulates pending tasks during this scan,
        // scan is considered successful when this queue is empty after processing
        final Queue<AbstractScanningTask> pendingTasks = new ArrayDeque<>();

        final Set<Pair<Class<?>, CtClass>> allClasses = this.getAllClasses(this.reflections, filter);
        for (final Pair<Class<?>, CtClass> entry : allClasses)
        {
            final Class<?> clazz = entry.getKey();
            if (clazz.isAnnotationPresent(SkipInjections.class))
            {
                continue;
            }

            final CtClass ctClass = entry.getValue();
            if (! clazz.isInterface() && ! clazz.isEnum())
            {
                // don't try to install injector in interfaces&enums,
                // because it's waste of time
                this.injectorInstaller.tryInstall(ctClass);
            }

            final AbstractBeanContext beanContext = this.manager.getOwningContext(clazz);

            // add all required tasks to the queue, so we can process them
            pendingTasks.add(new ProfileScanningTask(this, clazz, ctClass, beanContext));
            pendingTasks.add(new StaticScanningTask(this, clazz, ctClass, beanContext));
            pendingTasks.add(new ConstructorScanningTask(this, clazz, ctClass, beanContext));
            pendingTasks.add(new MethodScanningTask(this, clazz, ctClass, beanContext));
            if (clazz.isEnum())
            {
                // required to inject non-static fields in enums
                pendingTasks.add(new EnumScanningTask(this, clazz, ctClass, beanContext));
            }
        }

        this.processQueue(pendingTasks);
        if (! pendingTasks.isEmpty())
        {
            throw new RuntimeException(this.generateScanningError(pendingTasks));
        }

        for (final Pair<Class<?>, CtClass> entry : allClasses)
        {
            final AbstractBeanContext beanContext = this.manager.getOwningContext(entry.getKey());
            this.manager.getAggregationManager().call(beanContext, entry.getValue(), entry.getKey());
        }
    }

    private void processQueue(final Queue<AbstractScanningTask> pendingTasks)
    {
        boolean modified = true;
        while (modified)
        {
            modified = false;
            final Iterator<AbstractScanningTask> iterator = pendingTasks.iterator();
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

    private String generateScanningError(final Queue<AbstractScanningTask> pendingTasks)
    {
        final TextStringBuilder sb = new TextStringBuilder(512);
        sb.append("They're ").append(pendingTasks.size()).append(" uncompleted tasks! Below you will se trace of these tasks.").appendNewLine();
        for (final AbstractScanningTask task : pendingTasks)
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

    @SneakyThrows(NotFoundException.class)
    /*default*/ Set<Pair<Class<?>, CtClass>> getAllClasses(final Reflections reflections, final FilterBuilder filter)
    {
        final ClassPool classPool = this.weakClassPool.getClassPool();

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
                out.add(Pair.of(outClass, classPool.getCtClass(clazz)));
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
                
                Class<?> clazz = Class.forName(type, true, classLoader);
                
                // Musimy sie upewnic czy klasa rzeczywiscie zostala zaladowana przez dany classloader
                // Sytuacja kiedy classloader jest inny mozliwa jest kiedy klasa zostala zaladowana wczesniej przez inny classloader
                // ktory jest rodzicem classloader'a tego ktory zostal przekazany do metody forName.
                if ( clazz.getClassLoader() == classLoader )
                {
                    return clazz;
                }
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

    public ComponentManagerImpl getManager()
    {
        return this.manager;
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
