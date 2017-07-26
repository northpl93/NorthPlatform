package pl.north93.zgame.api.global.component;

import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;

public abstract class Component implements IBeanContext
{
    private ApiCore           apiCore;
    private IComponentManager manager;
    private IComponentBundle  componentBundle;
    private String            name;
    private boolean           isInitialised;

    protected Component()
    {
    }

    protected abstract void enableComponent();

    protected abstract void disableComponent();

    public final ComponentStatus getStatus()
    {
        return this.componentBundle.getStatus();
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

    @Override
    public String getBeanContextName()
    {
        return this.componentBundle.getBeanContext().getBeanContextName();
    }

    @Override
    public IBeanContext getParent()
    {
        return this.componentBundle.getBeanContext().getParent();
    }

    @Override
    public <T> T getBean(final Class<T> clazz)
    {
        return this.componentBundle.getBeanContext().getBean(clazz);
    }

    @Override
    public <T> T getBean(final String beanName)
    {
        return this.componentBundle.getBeanContext().getBean(beanName);
    }

    @Override
    public <T> T getBean(final IBeanQuery query)
    {
        return this.componentBundle.getBeanContext().getBean(query);
    }

    // = = = INTERNAL IMPLEMENTATION METHODS = = = //

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
    }

    public final void callStartMethod(final boolean isEnabling)
    {
        if (isEnabling)
        {
            this.enableComponent();
        }
        else
        {
            this.disableComponent();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("apiCore", this.apiCore).append("manager", this.manager).append("name", this.name).toString();
    }
}
