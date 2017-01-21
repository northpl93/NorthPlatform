package pl.north93.zgame.api.global.component;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.impl.Injector;

public abstract class Component
{
    private ApiCore           apiCore;
    private IComponentManager manager;
    private IComponentBundle  componentBundle;
    private String            name;
    private ComponentStatus   status;
    private boolean           isInitialised;

    protected Component()
    {
    }

    protected abstract void enableComponent();

    protected abstract void disableComponent();

    public final ComponentStatus getStatus()
    {
        return this.status;
    }

    public final String getName()
    {
        return this.name;
    }

    public final IComponentManager getComponentManager()
    {
        return this.manager;
    }

    protected final ApiCore getApiCore()
    {
        return this.apiCore;
    }

    protected final Logger getLogger()
    {
        return this.apiCore.getLogger();
    }

    protected final <T extends Component> T getComponent(final String name)
    {
        return this.manager.getComponent(name);
    }

    protected final Collection<? extends IExtensionPoint<?>> getExtensionPoints()
    {
        return this.componentBundle.getExtensionPoints();
    }

    protected final <T> IExtensionPoint<T> getExtensionPoint(final Class<T> clazz)
    {
        return this.componentBundle.getExtensionPoint(clazz);
    }

    public final void init(final IComponentBundle componentBundle, final IComponentManager componentManager, final ApiCore apiCore)
    {
        if (this.isInitialised)
        {
            throw new IllegalStateException("Component already initialised!");
        }
        this.isInitialised = true;
        this.componentBundle = componentBundle;
        this.name = componentBundle.getName();
        this.manager = componentManager;
        this.apiCore = apiCore;
        this.status = ComponentStatus.DISABLED;
    }

    public final void enable()
    {
        final String prettyPackages = this.componentBundle.getBasePackages().stream().collect(Collectors.joining(", "));
        this.apiCore.getLogger().info("Enabling component " + this.getName() + " (packages used to scan: " + prettyPackages + ")");
        try
        {
            Injector.inject(this.manager, this); // inject annotations
            this.componentBundle.doExtensionsScan();
            this.enableComponent();
        }
        catch (final Exception e)
        {
            this.status = ComponentStatus.ERROR;
            this.apiCore.getLogger().log(Level.SEVERE, "An exception has been thrown while enabling component " + this.getName(), e);
            return;
        }
        this.status = ComponentStatus.ENABLED;
    }

    public final void disable()
    {
        this.apiCore.getLogger().info("Disabling component " + this.getName());
        try
        {
            this.disableComponent();
        }
        catch (final Exception e)
        {
            this.status = ComponentStatus.ERROR;
            this.apiCore.getLogger().log(Level.SEVERE, "An exception has been thrown while disabling component " + this.getName(), e);
            return;
        }
        this.status = ComponentStatus.DISABLED;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("apiCore", this.apiCore).append("manager", this.manager).append("name", this.name).append("status", this.status).toString();
    }
}
