package pl.north93.zgame.restful.security;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Konfiguracja RESTowego API.
 */
@XmlRootElement(name = "rest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApiConfig
{
    @XmlElement
    private boolean      isSecurityEnabled;
    @XmlElementWrapper(name = "tokens")
    @XmlElement(name = "token")
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
