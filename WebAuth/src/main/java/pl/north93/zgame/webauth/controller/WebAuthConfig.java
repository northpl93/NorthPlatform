package pl.north93.zgame.webauth.controller;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "webauth")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebAuthConfig
{
    @XmlElement
    private String loginUrl;

    @XmlElement
    private int    keyLength;

    @XmlElement
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
