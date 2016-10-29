package pl.north93.zgame.api.bungee.cfg;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

@CfgComments({"Konfiguracja instancji BungeeCorda"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class ProxyInstanceConfig
{
    @CfgStringDefault("bungee1")
    private String uniqueName;

    public String getUniqueName()
    {
        return this.uniqueName;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uniqueName", this.uniqueName).toString();
    }
}
