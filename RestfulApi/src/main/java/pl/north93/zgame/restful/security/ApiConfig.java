package pl.north93.zgame.restful.security;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgCollectionStyle.CollectionStyle;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgBooleanDefault;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

public class ApiConfig
{
    @CfgComment("Czy wymagany jest token do uzywania API")
    @CfgBooleanDefault(false)
    private boolean      isSecurityEnabled;
    @CfgComment("Lista tokenow ktore przyjmuje API")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    @CfgDelegateDefault("{ArrayList}")
    private List<String> tokens;

    public boolean isSecurityEnabled()
    {
        return this.isSecurityEnabled;
    }

    public List<String> getTokens()
    {
        return this.tokens;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("isSecurityEnabled", this.isSecurityEnabled).append("tokens", this.tokens).toString();
    }
}
