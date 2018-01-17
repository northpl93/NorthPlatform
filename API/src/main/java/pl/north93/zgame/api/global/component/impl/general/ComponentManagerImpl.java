package pl.north93.zgame.api.global.component.impl.general;

import static java.text.MessageFormat.format;

import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import javax.xml.bind.JAXB;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reflections.Reflections;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.impl.aggregation.AggregationManager;
import pl.north93.zgame.api.global.component.impl.container.BeanFactory;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.context.ComponentBeanContext;
import pl.north93.zgame.api.global.component.impl.context.RootBeanContext;
import pl.north93.zgame.api.global.component.impl.profile.ProfileManagerImpl;
import pl.north93.zgame.api.global.component.impl.scanner.ClassloaderScanningTask;

public class ComponentManagerImpl implements IComponentManager
{
    public static ComponentManagerImpl instance;
    private final ApiCore               apiCore;
    private final List<ComponentBundle> components;
    private final ClassPool             rootClassPool;
    private final RootBeanContext       rootBeanCtx;
    private final ProfileManagerImpl    profileManager;
    private final AggregationManager    aggregationManager;
    private ClassloaderScanningTask rootScanningTask;
    private boolean autoEnable;
    private List<ClassLoader> scannedClassloaders = new ArrayList<>();

    public ComponentManagerImpl(final ApiCore apiCore)
    {
        instance = this;
        this.apiCore = apiCore;
        this.components = new ArrayList<>();

        this.rootClassPool = new ClassPool();
        this.rootClassPool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()));

        this.rootBeanCtx = new RootBeanContext();
        this.profileManager = new ProfileManagerImpl(this);
        this.aggregationManager = new AggregationManager();
    }

    public void initDefaultBeans()
    {
        final BeanFactory factory = BeanFactory.INSTANCE;
        factory.createStaticBeanManually(this.rootBeanCtx, this.apiCore.getClass(), "ApiCore", this.apiCore);
        factory.createStaticBeanManually(this.rootBeanCtx, Logger.class, "ApiLogger", this.apiCore.getLogger());

        if (this.apiCore.getPlatform() == Platform.BUKKIT)
        {
            factory.createStaticBeanManually(this.rootBeanCtx, JavaPlugin.class, "JavaPlugin", ((BukkitApiCore) this.apiCore).getPluginMain());
        }
    }

    private boolean canLoad(final ComponentDescription componentDescription)
    {
        return componentDescription.isEnabled() && ArrayUtils.contains(componentDescription.getPlatforms(), this.apiCore.getPlatform());
    }

    private void loadComponent(final ClassLoader classLoader, final ComponentDescription componentDescription)
    {
        if (! this.canLoad(componentDescription))
        {
            return; // skip loading of component
        }
        this.apiCore.getLogger().log(Level.INFO, "Loading component {0}", componentDescription.getName());

        final AbstractBeanContext componentBeanContext;
        if (classLoader instanceof JarComponentLoader)
        {
            componentBeanContext = new ComponentBeanContext(((JarComponentLoader) classLoader).getBeanContext(), componentDescription.getName());
        }
        else
        {
            componentBeanContext = this.rootBeanCtx;
        }

        final ComponentBundle componentBundle = new ComponentBundle(this, componentDescription, classLoader, componentBeanContext);
        this.components.add(componentBundle);
    }

    private boolean canEnableComponent(final ComponentBundle componentBundle)
    {
        final ComponentDescription description = componentBundle.getDescription();
        if (! componentBundle.canStart() || componentBundle.isEnabled())
        {
            return false;
        }
        for (final String dependency : description.getDependencies())
        {
            final ComponentBundle dependencyBundle = this.getComponentBundle(dependency);
            if (dependencyBundle == null || ! dependencyBundle.isEnabled())
            {
                return false;
            }
        }
        return true;
    }

    private ComponentBundle getComponentBundle(final String name)
    {
        return findInCollection(this.components, ComponentBundle::getName, name);
    }

    /**
     * Enables all dependencies of specified ComponentBundle.
     *
     * @param componentBundle ComponentBundle to check.
     * @return true - if all dependencies of specified ComponentBundle is present and enabled.
     *         false - if dependencies are not met.
     */
    private boolean checkAndEnableDependencies(final ComponentBundle componentBundle)
    {
        for (final String dependencyName : componentBundle.getDescription().getDependencies())
        {
            final ComponentBundle dependencyBundle = this.getComponentBundle(dependencyName);
            if (dependencyBundle == null) // can't find specified dependency.
            {
                return false;
            }
            // register dependencies in JarComponentLoader
            if (componentBundle.getClassLoader() instanceof JarComponentLoader && dependencyBundle.getClassLoader() instanceof JarComponentLoader)
            {
                final JarComponentLoader checkedLoader = (JarComponentLoader) componentBundle.getClassLoader();
                final JarComponentLoader dependencyLoader = (JarComponentLoader) dependencyBundle.getClassLoader();

                checkedLoader.registerDependency(dependencyLoader);
            }
            if (componentBundle.getBeanContext() instanceof ComponentBeanContext && dependencyBundle.getBeanContext() instanceof ComponentBeanContext)
            {
                ((ComponentBeanContext) componentBundle.getBeanContext()).addDependency(((ComponentBeanContext) dependencyBundle.getBeanContext()));
            }

            if (dependencyBundle.isEnabled()) // if dependencyBundle is already enabled, skip checking
            {
                continue;
            }
            if (! this.checkAndEnableDependencies(dependencyBundle)) // enable dependencies of currently checked bundle
            {
                return false;
            }
            if (! this.canEnableComponent(dependencyBundle)) // check if we can enable this component
            {
                return false;
            }
            dependencyBundle.enable();
        }
        return true;
    }

    @Override
    public void doComponentScan(final String componentsXml, final ClassLoader classLoader)
    {
        final ComponentsConfig componentsConfig = this.loadComponentsConfig(componentsXml, classLoader);
        if (componentsConfig == null)
        {
            return;
        }
        // splaszczamy strukture includowanych plikow do jednego obiektu
        this.flatComponentsConfig(classLoader, componentsConfig);

        // skanujemy elementy nie nalezace do zadnego komponentu
        this.scanClassloaderWithoutComponents(classLoader, componentsConfig.getExcludedPackages(), componentsConfig.getComponents());

        // skanujemy komponenty
        for (final ComponentDescription componentDescription : componentsConfig.getComponents())
        {
            this.loadComponent(classLoader, componentDescription);
        }
    }

    // splaszcza strukture includowanych plikow
    // po wykonaniu operacji lista includes bedzie pusta
    private void flatComponentsConfig(final ClassLoader loader, final ComponentsConfig componentsConfig)
    {
        final Iterator<String> includes = componentsConfig.getInclude().iterator();
        while (includes.hasNext())
        {
            final String include = includes.next();
            includes.remove();

            final ComponentsConfig includedConfig = this.loadComponentsConfig(include, loader);
            if (includedConfig == null)
            {
                // log?
                continue;
            }

            // najpierw splaszczamy config ktory zaladowalismy
            this.flatComponentsConfig(loader, includedConfig);

            // pozniej przenosimy z niego dane do configu 'wyzszego'
            componentsConfig.getExcludedPackages().addAll(includedConfig.getExcludedPackages());
            componentsConfig.getComponents().addAll(includedConfig.getComponents());
        }
    }

    private ComponentsConfig loadComponentsConfig(final String componentsXml, final ClassLoader classLoader)
    {
        final InputStream stream = classLoader.getResourceAsStream(componentsXml);
        if (stream == null)
        {
            return null;
        }
        return JAXB.unmarshal(stream, ComponentsConfig.class);
    }

    private void scanClassloaderWithoutComponents(final ClassLoader classLoader, final Set<String> excludedPackages, final List<ComponentDescription> components)
    {
        if (this.scannedClassloaders.contains(classLoader))
        {
            return;
        }

        this.getScanningTask(classLoader, excludedPackages).scanWithoutComponents(components);
        this.scannedClassloaders.add(classLoader);
    }

    @Override
    public void doComponentScan(final File file)
    {
        if (file.isFile())
        {
            this.loadComponentsFromFile(file);
        }
        else
        {
            final File[] files = file.listFiles();
            if (files == null)
            {
                return;
            }
            for (final File fileToCheck : files)
            {
                this.doComponentScan(fileToCheck);
            }
        }
    }

    private void loadComponentsFromFile(final File file)
    {
        this.apiCore.debug(format("Loading components from file {0}", file.getAbsolutePath()));

        final JarComponentLoader loader;
        try
        {
            loader = new JarComponentLoader(this.rootBeanCtx, file.toURI().toURL(), this.getClass().getClassLoader());
        }
        catch (final MalformedURLException e)
        {
            this.apiCore.getLogger().log(Level.SEVERE, "Failed to load components from file", e);
            return;
        }

        this.doComponentScan(loader);
    }

    @Override
    public void setAutoEnable(final boolean autoEnable)
    {
        this.autoEnable = autoEnable;
    }

    @Override
    public void enableAllComponents()
    {
        for (final ComponentBundle component : this.components)
        {
            if (component.isEnabled() || !component.canStart())
            {
                continue;
            }
            if (this.checkAndEnableDependencies(component))
            {
                component.enable();
            }
            else
            {
                this.apiCore.getLogger().warning("Can't resolve dependencies for " + component.getName());
            }
        }
    }

    @Override
    public void disableAllComponents()
    {
        final Map<ComponentBundle, Boolean> queue = new IdentityHashMap<>(this.components.size());
        for (final ComponentBundle component : this.components)
        {
            if (component.isEnabled())
            {
                queue.put(component, Boolean.TRUE); // fill queue enabled components
            }
        }

        while (! queue.isEmpty())
        {
            queue.forEach((component, canDisable) ->
            {
                for (final String depName : component.getDescription().getDependencies())
                {
                    final ComponentBundle dependency = this.getComponentBundle(depName);
                    if (dependency == null || !dependency.isEnabled())
                    {
                        continue;
                    }
                    queue.put(dependency, Boolean.FALSE);
                }
            });

            final Iterator<Map.Entry<ComponentBundle, Boolean>> iterator = queue.entrySet().iterator();
            while (iterator.hasNext())
            {
                final Map.Entry<ComponentBundle, Boolean> entry = iterator.next();
                if (entry.getValue()) // canDisable
                {
                    entry.getKey().disable();
                    iterator.remove();
                }
                else
                {
                    entry.setValue(Boolean.TRUE);
                }
            }
        }
    }

    @Override
    public <T extends Component> T getComponent(final String name)
    {
        final ComponentBundle bundle = findInCollection(this.components, ComponentBundle::getName, name);
        if (bundle == null)
        {
            throw new IllegalArgumentException("Not found component with name " + name);
        }
        //noinspection unchecked
        return (T) bundle.getComponent();
    }

    @Override
    public Collection<? extends IComponentBundle> getComponents()
    {
        return Collections.unmodifiableList(this.components);
    }

    @Override
    public ProfileManagerImpl getProfileManager()
    {
        return this.profileManager;
    }

    @Override
    public Reflections accessReflections(final ClassLoader classLoader)
    {
        if (classLoader.equals(this.getClass().getClassLoader()))
        {
            return this.rootScanningTask.getReflections();
        }
        else if (classLoader instanceof JarComponentLoader)
        {
            return ((JarComponentLoader) classLoader).getScanningTask().getReflections();
        }
        throw new IllegalArgumentException("Invalid classloader");
    }

    /**
     * Zwraca kontekst do którego należy dana klasa.
     * @param clazz klasa dla której sprawdzić kontekst
     * @return kontekst danej klasy.
     */
    public AbstractBeanContext getOwningContext(final Class<?> clazz)
    {
        final ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == this.getClass().getClassLoader())
        {
            return this.rootBeanCtx;
        }

        final String packageName = clazz.getPackage().getName();
        for (final ComponentBundle component : this.components)
        {
            if (component.getClassLoader() != classLoader)
            {
                continue;
            }

            for (final String basePackage : component.getBasePackages())
            {
                if (packageName.startsWith(basePackage))
                {
                    return component.getBeanContext();
                }
            }
        }

        if (classLoader instanceof JarComponentLoader)
        {
            return ((JarComponentLoader) classLoader).getBeanContext();
        }

        throw new IllegalArgumentException("Not found bean context for class " + clazz.getName());
    }

    /**
     * Zwraca javassistowego classpola dla danego ClassLoadera.
     * @param classLoader classloader z którego utworzyć poola.
     * @return zcachowany ClassPool.
     */
    public ClassPool getClassPool(final ClassLoader classLoader)
    {
        if (classLoader instanceof JarComponentLoader)
        {
            final JarComponentLoader componentLoader = (JarComponentLoader) classLoader;
            return componentLoader.getClassPool();
        }
        else if (classLoader == this.getClass().getClassLoader())
        {
            return this.rootClassPool;
        }
        throw new IllegalArgumentException("Unknown classloader: " + classLoader);
    }

    public ClassloaderScanningTask getScanningTask(final ClassLoader classLoader, final Set<String> excludedPackages)
    {
        if (classLoader instanceof JarComponentLoader)
        {
            final JarComponentLoader componentLoader = (JarComponentLoader) classLoader;
            if (componentLoader.getScanningTask() == null)
            {
                final ClassloaderScanningTask task = ClassloaderScanningTask.create(this, classLoader, excludedPackages);
                componentLoader.setScanningTask(task);
                return task;
            }
            return componentLoader.getScanningTask();
        }
        else
        {
            if (this.rootScanningTask == null)
            {
                this.rootScanningTask = ClassloaderScanningTask.create(this, this.getClass().getClassLoader(), excludedPackages);
            }
            return this.rootScanningTask;
        }
    }

    public AggregationManager getAggregationManager()
    {
        return this.aggregationManager;
    }

    // zwraca instancje ApiCore powiazana z tym menadzerem komponentow
    public ApiCore getApiCore()
    {
        return this.apiCore;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("components", this.components).append("autoEnable", this.autoEnable).toString();
    }
}
