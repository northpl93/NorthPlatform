package pl.north93.northplatform.api.economy.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.economy.ICurrency;

@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyConfig implements ICurrency
{
    @XmlElement
    private String name;
    @XmlElement
    private Double startValue;

    public CurrencyConfig()
    {
    }

    public CurrencyConfig(final String name, final Double startValue)
    {
        this.name = name;
        this.startValue = startValue;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public double getStartValue()
    {
        return this.startValue;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("startValue", this.startValue).toString();
    }
}
