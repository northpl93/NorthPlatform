package pl.arieals.api.minigame.shared.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "voting")
@XmlAccessorType(XmlAccessType.FIELD)
public class MapVotingConfig
{
    @XmlElement
    private Boolean enabled; // Czy wlaczone
    @XmlElement
    private Integer numberOfMaps; // Ilosc map do wyboru podczas glosowania.

    public Boolean getEnabled()
    {
        return this.enabled;
    }

    public Integer getNumberOfMaps()
    {
        return this.numberOfMaps;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enabled", this.enabled).append("numberOfMaps", this.numberOfMaps).toString();
    }
}
