package pl.north93.zgame.api.global.component.impl;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.IComponentManager;

public class ComponentManagerImpl implements IComponentManager
{
    private final ApiCore                    apiCore;
    private final Template<ComponentsConfig> configTemplate = TemplateCreator.getTemplate(ComponentsConfig.class);
    private final List<ComponentBundle>      components     = new ArrayList<>();
    private boolean autoEnable;

    public ComponentManagerImpl(final ApiCore apiCore)
    {
        this.apiCore = apiCore;
    }

    private void initComponent(final ComponentBundle component)
    {
        component.getComponent().init(component.getDescription().getName(), this, API.getApiCore()); // TODO
        if (this.autoEnable && this.checkAndEnableDependencies(component))
        {
            component.getComponent().enable();
        }
    }

    private void loadComponent(final ClassLoader classLoader, final ComponentDescription componentDescription)
    {
        //System.out.println("loadComponent(" + classLoader + ", " + componentDescription + ")");
        if (! componentDescription.isEnabled())
        {
            return; // skip loading of disabled component
        }
        this.apiCore.getLogger().info("Loading component " + componentDescription.getName());

        final ComponentBundle componentBundle = new ComponentBundle(componentDescription, classLoader);
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
        System.out.println("doComponentScan(" + componentsYml + ", " + classLoader + ")");
        final InputStream stream = classLoader.getResourceAsStream(componentsYml);
        if (stream == null)
        {
            return;
        }
        final Reader reader = new InputStreamReader(stream);
        final ComponentsConfig componentsConfig = this.configTemplate.load(reader, classLoader);
        for (final ComponentDescription componentDescription : componentsConfig.getComponents())
        {
            this.loadComponent(classLoader, componentDescription);
        }
    }

    @Override
    public void doComponentScan(final File file)
    {

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
                System.out.println("Can't resolve dependencies for " + component.getName());
            }
        }
    }

    @Override
    public void disableAllComponents()
    {
        for (final ComponentBundle component : this.components)
        {
            if (! component.isEnabled())
            {
                continue;
            }
            component.getComponent().disable();
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
}
