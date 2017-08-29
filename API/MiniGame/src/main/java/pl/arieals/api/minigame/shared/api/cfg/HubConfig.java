package pl.arieals.api.minigame.shared.api.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.JoiningPolicy;

@XmlRootElement(name = "hub")
@XmlAccessorType(XmlAccessType.FIELD)
public class HubConfig
{
    @XmlElement
    private String        hubId;
    @XmlElement
    private String        worldName;
    @XmlElement
    private JoiningPolicy joiningPolicy;

    public String getHubId()
    {
        return this.hubId;
    }

    public String getWorldName()
    {
        return this.worldName;
    }

    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("hubId", this.hubId).append("joiningPolicy", this.joiningPolicy).toString();
    }
}
