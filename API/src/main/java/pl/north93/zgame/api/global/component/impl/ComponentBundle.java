package pl.north93.zgame.api.global.component.impl;

import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.ComponentStatus;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;

class ComponentBundle extends AbstractBeanContext implements IComponentBundle
{
    private final String               name;
    private final ComponentDescription description;
    private final ClassLoader          classLoader;
    private Set<String> basePackages;
    private Component   component;

    public ComponentBundle(final ComponentDescription description, final ClassLoader classLoader, final AbstractBeanContext parentBeanContext)
    {
        super(parentBeanContext, "Component-" + description.getName());
        this.name = description.getName();
        this.description = description;
        this.classLoader = classLoader;
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
        return this.component.getStatus();
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
    }

    public boolean isEnabled()
    {
        return this.component != null && this.component.getStatus().isEnabled();
    }

    public boolean canStart()
    {
        if (this.component == null)
        {
            return false;
        }

        if (! this.component.getStatus().equals(ComponentStatus.DISABLED))
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("description", this.description).append("classLoader", this.classLoader).append("component", this.component).toString();
    }
}
