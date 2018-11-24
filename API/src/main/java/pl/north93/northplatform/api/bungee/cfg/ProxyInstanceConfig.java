package pl.north93.northplatform.api.bungee.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "proxyInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProxyInstanceConfig
{
    @XmlElement
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
