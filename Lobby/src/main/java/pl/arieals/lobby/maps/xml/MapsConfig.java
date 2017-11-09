package pl.arieals.lobby.maps.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlSeeAlso({RankingMapConfig.class}) // tu trzeba dopisywac inne typy mapy
@XmlRootElement(name = "maps")
@XmlAccessorType(XmlAccessType.FIELD)
public final class MapsConfig
{
    @XmlElement
    @XmlAnyElement(lax = true)
    private List<MapConfig> maps;

    public List<MapConfig> getMaps()
    {
        return this.maps;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("maps", this.maps).toString();
    }
}

