package pl.north93.zgame.api.economy.cfg;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;

import pl.north93.zgame.api.economy.ICurrency;

public class CurrencyConfig implements ICurrency
{
    @CfgComment("Nazwa wewnętrzna waluty")
    private String  name;
    @CfgComment("Startowa ilość waluty")
    @CfgIntDefault(0)
    private Integer startValue;

    public CurrencyConfig()
    {
    }

    public CurrencyConfig(final String name, final Integer startValue)
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
    public int getStartValue()
    {
        return this.startValue;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("startValue", this.startValue).toString();
    }
}
