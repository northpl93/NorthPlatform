package pl.north93.zgame.webauth.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

@CfgComment("Konfiguracja komendy do logowania przez przegladarke")
public class WebAuthConfig
{
    @CfgComment("Adres URL do logowania")
    @CfgStringDefault("http://example.com/{0}")
    private String loginUrl;

    @CfgComment("Dlugosc generowanego klucza")
    @CfgIntDefault(32)
    private int    keyLength;

    @CfgComment("Czas w sekundach po ktorych klucz wygasa")
    @CfgIntDefault(5 * 60)
    private int    expireTime;

    public String getLoginUrl()
    {
        return this.loginUrl;
    }

    public int getKeyLength()
    {
        return this.keyLength;
    }

    public int getExpireTime()
    {
        return this.expireTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("loginUrl", this.loginUrl).append("keyLength", this.keyLength).append("expireTime", this.expireTime).toString();
    }
}
