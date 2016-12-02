package pl.north93.zgame.api.global.component.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.ComponentStatus;

public class ComponentBundle
{
    private final String               name;
    private final ComponentDescription description;
    private final ClassLoader          classLoader;
    private Component component;

    public ComponentBundle(final ComponentDescription description, final ClassLoader classLoader)
    {
        this.name = description.getName();
        this.description = description;
        this.classLoader = classLoader;
    }

    public String getName()
    {
        return this.name;
    }

    public ComponentDescription getDescription()
    {
        return this.description;
    }

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
