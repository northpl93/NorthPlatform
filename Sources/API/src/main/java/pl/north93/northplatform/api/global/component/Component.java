package pl.north93.northplatform.api.global.component;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.ApiCore;

public abstract class Component implements IBeanContext
{
    private ApiCore apiCore;
    private IComponentBundle componentBundle;
    private String name;
    private boolean isInitialised;

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

    public final ApiCore getApiCore()
    {
        return this.apiCore;
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
    public boolean isBeanExists(final Class<?> clazz)
    {
        return this.componentBundle.getBeanContext().isBeanExists(clazz);
    }

    @Override
    public <T> T getBean(final String beanName)
    {
        return this.componentBundle.getBeanContext().getBean(beanName);
    }

    @Override
    public boolean isBeanExists(final String beanName)
    {
        return this.componentBundle.getBeanContext().isBeanExists(beanName);
    }

    @Override
    public <T> T getBean(final IBeanQuery query)
    {
        return this.componentBundle.getBeanContext().getBean(query);
    }

    @Override
    public boolean isBeanExists(final IBeanQuery query)
    {
        return this.componentBundle.getBeanContext().isBeanExists(query);
    }

    // = = = INTERNAL IMPLEMENTATION METHODS = = = //

    public final void init(final IComponentBundle componentBundle, final ApiCore apiCore)
    {
        if (this.isInitialised)
        {
            throw new IllegalStateException("Component already initialised!");
        }
        this.isInitialised = true;
        this.componentBundle = componentBundle;
        this.name = componentBundle.getName();
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("apiCore", this.apiCore).append("name", this.name).toString();
    }
}
