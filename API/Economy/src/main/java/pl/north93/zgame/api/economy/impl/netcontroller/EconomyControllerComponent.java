package pl.north93.zgame.api.economy.impl.netcontroller;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.cfg.EconomyConfig;
import pl.north93.zgame.api.economy.impl.shared.EconomyManagerImpl;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;

@IncludeInScanning("pl.north93.zgame.api.economy.impl.shared")
public class EconomyControllerComponent extends Component
{
    private EconomyConfig      config;
    private EconomyManagerImpl economyManager;

    @Override
    protected void enableComponent()
    {
        this.config = loadConfigFile(EconomyConfig.class, this.getApiCore().getFile("economy.yml"));
        this.economyManager = new EconomyManagerImpl();
        this.economyManager.setConfig(this.config);
    }

    @Override
    protected void disableComponent()
    {
    }

    public EconomyConfig getConfig()
    {
        return this.config;
    }

    public EconomyManagerImpl getEconomyManager()
    {
        return this.economyManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("config", this.config).append("economyManager", this.economyManager).toString();
    }
}
