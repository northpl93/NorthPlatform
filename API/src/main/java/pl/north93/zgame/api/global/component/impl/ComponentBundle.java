package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.ComponentStatus;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;
import pl.north93.zgame.api.global.component.impl.container.BeanFactory;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;

public class ComponentBundle implements IComponentBundle
{
    private final String               name;
    private final ComponentDescription description;
    private final ClassLoader          classLoader;
    private final AbstractBeanContext  componentBeanContext;
    private       Set<String>          basePackages;
    private       Component            component;
    private       ComponentStatus      status;

    public ComponentBundle(final ComponentDescription description, final ClassLoader classLoader, final AbstractBeanContext componentBeanContext)
    {
        this.name = description.getName();
        this.description = description;
        this.classLoader = classLoader;
        this.componentBeanContext = componentBeanContext;
        this.basePackages = new ObjectArraySet<>();
    }

    @Override
    public Set<String> getBasePackages()
    {
        if (this.basePackages.isEmpty())
        {
            final String mainClass = this.description.getMainClass();

            if (StringUtils.isEmpty(this.description.getPackageToScan()))
            {
                final int lastIndexOfDot = mainClass.lastIndexOf(".");
                this.basePackages.add(mainClass.substring(0, lastIndexOfDot));
            }
            else
            {
                this.basePackages.add(this.description.getPackageToScan());
            }

            try
            {
                final Class<?> clazz = Class.forName(mainClass, true, this.classLoader);
                for (final IncludeInScanning includeInScanning : clazz.getAnnotationsByType(IncludeInScanning.class))
                {
                    this.basePackages.add(includeInScanning.value());
                }
            }
            catch (final Exception ignored)
            {
            }
        }

        return Sets.newCopyOnWriteArraySet(this.basePackages);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public ComponentStatus getStatus()
    {
        return this.status;
    }

    @Override
    public boolean isBuiltinComponent()
    {
        return ! (this.classLoader instanceof JarComponentLoader);
    }

    @Override
    public ComponentDescription getDescription()
    {
        return this.description;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return this.classLoader;
    }

    public final void enable()
    {
        final String prettyPackages = this.getBasePackages().stream().collect(Collectors.joining(", "));
        API.getLogger().info("Enabling component " + this.getName() + " (packages used to scan: " + prettyPackages + ")");
        try
        {
            this.instantiateClass(); // tworzymy klase glowna
            this.scanNow(); // wykonujemy wszystkie skanowania
            Injector.inject(this.component); // wstrzykujemy klase glowna
            this.getComponent().callStartMethod(true); // wywolujemy enableComponent
        }
        catch (final Exception e)
        {
            this.status = ComponentStatus.ERROR;
            API.getLogger().log(Level.SEVERE, "An exception has been thrown while enabling component " + this.getName(), e);
            return;
        }
        this.status = ComponentStatus.ENABLED;
    }

    public final void disable()
    {
        API.getLogger().info("Disabling component " + this.getName());
        try
        {
            this.getComponent().callStartMethod(false);
        }
        catch (final Exception e)
        {
            this.status = ComponentStatus.ERROR;
            API.getLogger().log(Level.SEVERE, "An exception has been thrown while disabling component " + this.getName(), e);
            return;
        }
        this.status = ComponentStatus.DISABLED;
    }

    public Component getComponent()
    {
        return this.component;
    }

    public void setComponent(final Component component)
    {
        if (this.component != null)
        {
            throw new IllegalStateException("ComponentBundle already has associated component.");
        }
        this.component = component;
        final Class<? extends Component> aClass = component.getClass();
        BeanFactory.INSTANCE.createStaticBeanManually(this.componentBeanContext, aClass, aClass.getName(), component);
    }

    public boolean isEnabled()
    {
        return this.component != null && this.status.isEnabled();
    }

    public boolean canStart()
    {
        if (this.component == null)
        {
            // komponent nie zostal jeszcze utworzony, zezwalamy na start.
            return true;
        }
        return this.component.getStatus().equals(ComponentStatus.DISABLED);
    }

    @Override
    public AbstractBeanContext getBeanContext()
    {
        return this.componentBeanContext;
    }

    private void instantiateClass()
    {
        final Component newComponent;
        try
        {
            final Class<?> clazz = Class.forName(this.description.getMainClass(), true, this.classLoader);
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            newComponent = (Component) constructor.newInstance();
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new RuntimeException("Failed to instantiate main class of " + this.description.getName(), e);
        }

        this.setComponent(newComponent);
        newComponent.init(this, ComponentManagerImpl.instance, API.getApiCore());
    }

    @Override
    public void scanNow()
    {
        // rootPackage moze byc nullem bo na pewno juz mamy utworzony ScanningTask.
        ComponentManagerImpl.instance.getScanningTask(this.classLoader, null).scanComponent(this);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("description", this.description).append("classLoader", this.classLoader).append("component", this.component).toString();
    }
}
