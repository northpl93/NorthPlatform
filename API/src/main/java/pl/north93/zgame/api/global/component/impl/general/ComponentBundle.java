package pl.north93.zgame.api.global.component.impl.general;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.ComponentStatus;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.impl.container.BeanFactory;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.zgame.api.global.component.impl.injection.Injector;

public class ComponentBundle implements IComponentBundle
{
    private final Logger               logger = LoggerFactory.getLogger(ComponentBundle.class);
    private final ComponentManagerImpl componentManager;
    private final ComponentDescription description;
    private final ClassLoader          classLoader;
    private final AbstractBeanContext  componentBeanContext;
    private final Set<String>          basePackages;
    private       Component            component;
    private       ComponentStatus      status;

    public ComponentBundle(final ComponentManagerImpl componentManager, final ComponentDescription description, final ClassLoader classLoader, final AbstractBeanContext componentBeanContext)
    {
        this.componentManager = componentManager;
        this.description = description;
        this.classLoader = classLoader;
        this.componentBeanContext = componentBeanContext;
        this.basePackages = this.createPackageList(); // inicjujemy liste paczek
        this.status = ComponentStatus.DISABLED; // prevent NPEs
    }

    @Override
    public Set<String> getBasePackages()
    {
        return Sets.newCopyOnWriteArraySet(this.basePackages);
    }

    @Override
    public String getName()
    {
        return this.description.getName();
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
        this.logger.info("Enabling component {} (packages used to scan: {})", this.getName(), prettyPackages);
        try
        {
            this.instantiateClass(); // tworzymy klase glowna
            this.scanNow(); // wykonujemy wszystkie skanowania
            Injector.inject(this.component, this.component.getClass()); // wstrzykujemy klase glowna
            this.getComponent().callStartMethod(true); // wywolujemy enableComponent
        }
        catch (final Exception e)
        {
            this.status = ComponentStatus.ERROR;
            this.logger.error("An exception has been thrown while enabling component {}", this.getName(), e);
            return;
        }
        this.status = ComponentStatus.ENABLED;
    }

    public final void disable()
    {
        this.logger.info("Disabling component {}", this.getName());
        try
        {
            this.getComponent().callStartMethod(false);
        }
        catch (final Exception e)
        {
            this.status = ComponentStatus.ERROR;
            this.logger.error("An exception has been thrown while disabling component {}", this.getName(), e);
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

    private Set<String> createPackageList()
    {
        final Set<String> packages = new HashSet<>();

        final String mainClass = this.description.getMainClass();
        if (this.description.getPackages().isEmpty())
        {
            final int lastIndexOfDot = mainClass.lastIndexOf(".");
            packages.add(mainClass.substring(0, lastIndexOfDot));
        }
        else
        {
            packages.addAll(this.description.getPackages());
        }

        return packages;
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
        newComponent.init(this, this.getApiCore());
    }

    private void scanNow()
    {
        // excludedPackages moze byc nullem bo na pewno juz mamy utworzony ScanningTask.
        this.componentManager.getScanningTask(this.classLoader, null).scanComponent(this);
    }

    private ApiCore getApiCore()
    {
        return this.componentManager.getApiCore();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("description", this.description).append("classLoader", this.classLoader).append("component", this.component).toString();
    }
}
