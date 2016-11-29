package pl.north93.zgame.api.global.component;

import pl.north93.zgame.api.global.ApiCore;

public abstract class Component
{
    private ApiCore           apiCore;
    private IComponentManager manager;
    private ComponentStatus   status;

    protected Component()
    {
    }

    protected abstract void enableComponent();

    protected abstract void disableComponent();

    protected final ApiCore getApiCore()
    {
        return this.apiCore;
    }

    /*default*/ final void init(final ApiCore apiCore)
    {
        this.apiCore = apiCore;
        this.status = ComponentStatus.DISABLED;
    }

    /*default*/ final void enable()
    {
        try
        {
            this.enableComponent();
        }
        catch (final Exception e)
        {
            this.status = ComponentStatus.ERROR;
            e.printStackTrace();
            return;
        }
        this.status = ComponentStatus.ENABLED;
    }

    /*default*/ final void disable()
    {
        try
        {
            this.disableComponent();
        }
        catch (final Exception e)
        {
            this.status = ComponentStatus.ERROR;
            e.printStackTrace();
            return;
        }
        this.status = ComponentStatus.DISABLED;
    }
}
