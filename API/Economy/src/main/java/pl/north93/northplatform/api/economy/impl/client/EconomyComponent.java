package pl.north93.northplatform.api.economy.impl.client;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.economy.impl.shared.EconomyManagerImpl;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class EconomyComponent extends Component
{
    @Inject
    private EconomyManagerImpl economyManager;

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }

    public IEconomyManager getEconomyManager()
    {
        return this.economyManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("economyManager", this.economyManager).toString();
    }
}
