package pl.north93.zgame.api.economy.impl.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "vault")
public class VaultConfig
{
    @XmlElement
    private boolean enableVaultIntegration;

    @XmlElement
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
