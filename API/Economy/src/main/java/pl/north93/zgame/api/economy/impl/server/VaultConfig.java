package pl.north93.zgame.api.economy.impl.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.defaults.CfgBooleanDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

public class VaultConfig
{
    @CfgBooleanDefault(false)
    private boolean enableVaultIntegration;

    @CfgStringDefault("setup")
    private String  currencyName;

    public boolean isEnableVaultIntegration()
    {
        return this.enableVaultIntegration;
    }

    public String getCurrencyName()
    {
        return this.currencyName;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enableVaultIntegration", this.enableVaultIntegration).append("currencyName", this.currencyName).toString();
    }
}
