package pl.north93.northplatform.api.minigame.shared.api.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "voting")
@XmlAccessorType(XmlAccessType.NONE)
public class ReconnectConfig
{
    @XmlElement
    private Boolean enabled;
    @XmlElement
    private Integer maxTime;

    public Boolean getEnabled()
    {
        return this.enabled;
    }

    public Integer getMaxTime()
    {
        return this.maxTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enabled", this.enabled).append("maxTime", this.maxTime).toString();
    }
}
