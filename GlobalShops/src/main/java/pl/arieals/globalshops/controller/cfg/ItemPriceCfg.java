package pl.arieals.globalshops.controller.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "price")
@XmlAccessorType(XmlAccessType.NONE)
public class ItemPriceCfg
{
    @XmlAttribute
    private Integer level = 1;
    @XmlAttribute(name = "currency", required = true)
    private String currencyId;
    @XmlAttribute(required = true)
    private Double amount;

    public Integer getLevel()
    {
        return this.level;
    }

    public String getCurrencyId()
    {
        return this.currencyId;
    }

    public Double getAmount()
    {
        return this.amount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currencyId", this.currencyId).append("amount", this.amount).toString();
    }
}
