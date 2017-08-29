package pl.arieals.api.minigame.shared.api.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "hubs")
@XmlAccessorType(XmlAccessType.FIELD)
public class HubsConfig
{
    @XmlElement
    private String          mainHub; // id (z ponizszej listy) huba na ktorym domyslnie laduje gracze, id mapy to 0
    @XmlElement(name = "hub")
    private List<HubConfig> hubs = new ArrayList<>(); // lista hubow na kazdym serwerze hostujacym.

    public String getMainHub()
    {
        return this.mainHub;
    }

    public List<HubConfig> getHubs()
    {
        return this.hubs;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mainHub", this.mainHub).append("hubs", this.hubs).toString();
    }
}
