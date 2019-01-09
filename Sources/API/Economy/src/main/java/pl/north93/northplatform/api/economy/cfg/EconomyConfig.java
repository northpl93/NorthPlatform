package pl.north93.northplatform.api.economy.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "economy")
@XmlAccessorType(XmlAccessType.FIELD)
public class EconomyConfig
{
    @XmlElementWrapper(name = "currencies")
    @XmlElement(name = "currency")
    private List<CurrencyConfig> currencies;

    public List<CurrencyConfig> getCurrencies()
    {
        return this.currencies;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currencies", this.currencies).toString();
    }
}
