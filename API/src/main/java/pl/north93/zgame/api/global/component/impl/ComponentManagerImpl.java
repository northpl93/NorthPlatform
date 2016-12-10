package pl.north93.zgame.api.global.component.impl;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import org.diorite.cfg.system.TemplateCreator;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.IComponentManager;

public class ComponentManagerImpl implements IComponentManager
{
    private final ApiCore               apiCore;
    private final List<ComponentBundle> components = new ArrayList<>();
    private boolean autoEnable;

    public ComponentManagerImpl(final ApiCore apiCore)
    {
        this.apiCore = apiCore;
    }

    private void initComponent(final ComponentBundle component)
    {
        component.getComponent().init(component, this, this.apiCore);
        if (this.autoEnable && this.checkAndEnableDependencies(component))
        {
            component.getComponent().enable();
        }
    }

    private void loadComponent(final ClassLoader classLoader, final ComponentDescription componentDescription)
    {
        if (! componentDescription.isEnabled() || ! ArrayUtils.contains(componentDescription.getPlatforms(), this.apiCore.getPlatform()))
        {
            return; // skip loading of component
        }
        this.apiCore.getLogger().info("Loading component " + componentDescription.getName());

        final List<ExtensionPointImpl<?>> extensionPoints = new ArrayList<>();
        for (final String extensionClassName : componentDescription.getExtensionPoints())
        {
            final Class<?> extensionClass;
            try
            {
                extensionClass = Class.forName(extensionClassName, true, classLoader);
            }
            catch (final ClassNotFoundException e)
            {
                e.printStackTrace();
                continue;
            }

            if (!extensionClass.isInterface() && !Modifier.isAbstract(extensionClass.getModifiers()))
            {
                this.apiCore.getLogger().warning("Class " + extensionClassName + " must be interface or abstract!");
                continue;
            }

            this.apiCore.getLogger().info("Component " + componentDescription.getName() + " exposes extension point: " + extensionClassName);
            extensionPoints.add(new ExtensionPointImpl<>(extensionClass));
        }

        final ComponentBundle componentBundle = new ComponentBundle(componentDescription, classLoader, extensionPoints);
        if (componentDescription.isAutoInstantiate())
        {
            try
            {
                final Class<?> clazz = Class.forName(componentDescription.getMainClass(), true, classLoader);
                final Component newComponent = (Component) clazz.newInstance();
                componentBundle.setComponent(newComponent);
            }
            catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e)
            {
                throw new RuntimeException(e);
            }

            this.initComponent(componentBundle);
        }
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
        for (final ComponentBundle component : this.components)
        {
            if (component.getName().equals(name))
            {
                return component;
            }
        }
        return null;
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
            dependencyBundle.getComponent().enable();
        }
        return true;
    }

    @Override
    public void doComponentScan(final String componentsYml, final ClassLoader classLoader)
    {
        final InputStream stream = classLoader.getResourceAsStream(componentsYml);
        if (stream == null)
        {
            return;
        }
        final Reader reader = new InputStreamReader(stream);
        final ComponentsConfig componentsConfig = TemplateCreator.getTemplate(ComponentsConfig.class).load(reader, classLoader);
        for (final ComponentDescription componentDescription : componentsConfig.getComponents())
        {
            this.loadComponent(classLoader, componentDescription);
        }

        for (final String includeConfig : componentsConfig.getInclude())
        {
            this.doComponentScan(includeConfig, classLoader);
        }
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
        final JarComponentLoader loader;
        try
        {
            loader = new JarComponentLoader(file.toURI().toURL(), this.getClass().getClassLoader());
        }
        catch (final MalformedURLException e)
        {
            e.printStackTrace();
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
    public void injectComponent(final Object object)
    {
        final Component component = (Component) object; // todo pretty warning if this fails
        final String className = component.getClass().getName();
        for (final ComponentBundle componentBundle : this.components)
        {
            if (componentBundle.canStart())
            {
                continue;
            }
            if (componentBundle.getDescription().getMainClass().equals(className))
            {
                componentBundle.setComponent(component);
                this.initComponent(componentBundle);
                return;
            }
        }
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
                component.getComponent().enable();
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
                    entry.getKey().getComponent().disable();
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
        for (final ComponentBundle component : this.components)
        {
            if (component.getName().equals(name))
            {
                //noinspection unchecked
                return (T) component.getComponent();
            }
        }
        return null;
    }

    @Override
    public Collection<? extends IComponentBundle> getComponents()
    {
        return Collections.unmodifiableList(this.components);
    }
}
