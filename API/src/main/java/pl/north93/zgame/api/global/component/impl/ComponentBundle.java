package pl.north93.zgame.api.global.component.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.ComponentDescription;
import pl.north93.zgame.api.global.component.ComponentStatus;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.IExtensionPoint;

class ComponentBundle implements IComponentBundle
{
    private final String               name;
    private final ComponentDescription description;
    private final ClassLoader          classLoader;
    private final List<ExtensionPointImpl<?>> extensionPoints;
    private String    basePackage;
    private Component component;

    public ComponentBundle(final ComponentDescription description, final ClassLoader classLoader, final List<ExtensionPointImpl<?>> extensionPoints)
    {
        this.name = description.getName();
        this.description = description;
        this.classLoader = classLoader;
        this.extensionPoints = extensionPoints;
    }

    @Override
    public String getBasePackage()
    {
        if (StringUtils.isEmpty(this.basePackage))
        {
            if (StringUtils.isEmpty(this.description.getPackageToScan()))
            {
                final String mainClass = this.description.getMainClass();
                final int lastIndexOfDot = mainClass.lastIndexOf(".");
                this.basePackage = mainClass.substring(0, lastIndexOfDot);
            }
            else
            {
                this.basePackage = this.description.getPackageToScan();
            }
        }
        return this.basePackage;
    }

    @Override
    public String getName()
    {
        return this.name;
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

    @Override
    public List<ExtensionPointImpl<?>> getExtensionPoints()
    {
        return this.extensionPoints;
    }

    @Override
    public <T> IExtensionPoint<T> getExtensionPoint(final Class<T> clazz)
    {
        for (final ExtensionPointImpl<?> extensionPoint : this.extensionPoints)
        {
            if (clazz == extensionPoint.getExtensionPointClass())
            {
                //noinspection unchecked
                return (IExtensionPoint<T>) extensionPoint;
            }
        }
        return null;
    }

    @Override
    public void doExtensionsScan()
    {
        if (this.isBuiltinComponent())
        {
            return;
        }
        ((JarComponentLoader) this.classLoader).scan(this.component.getComponentManager(), this.getBasePackage());
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
