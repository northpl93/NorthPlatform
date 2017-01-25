package pl.north93.zgame.api.economy.cfg;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

@CfgComment("Konfiguracja ekonomii")
public class EconomyConfig
{
    @CfgDelegateDefault("getDefaultCurrencies")
    private List<CurrencyConfig> currencies;

    public List<CurrencyConfig> getCurrencies()
    {
        return this.currencies;
    }

    private static List<CurrencyConfig> getDefaultCurrencies()
    {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(new CurrencyConfig("example", 0));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currencies", this.currencies).toString();
    }
}
