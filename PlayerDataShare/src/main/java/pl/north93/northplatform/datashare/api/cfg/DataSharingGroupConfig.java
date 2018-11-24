package pl.north93.northplatform.datashare.api.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataSharingGroupConfig
{
    @XmlElement
    private String          name;
    @XmlElementWrapper(name = "serversGroups")
    @XmlElement(name = "serversGroup")
    private List<String>    serversGroups;
    @XmlElementWrapper(name = "servers")
    @XmlElement(name = "server")
    private List<String>    servers;
    @XmlElement
    private boolean         shareChat;
    @XmlElement
    private AnnouncerConfig announcer;
    @XmlElementWrapper(name = "dataUnits")
    @XmlElement(name = "dataUnit")
    private List<String>    dataUnits;

    public String getName()
    {
        return this.name;
    }

    public List<String> getServersGroups()
    {
        return this.serversGroups;
    }

    public List<String> getServers()
    {
        return this.servers;
    }

    public AnnouncerConfig getAnnouncer()
    {
        return this.announcer;
    }

    public boolean isShareChat()
    {
        return this.shareChat;
    }

    public List<String> getDataUnits()
    {
        return this.dataUnits;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("serversGroups", this.serversGroups).append("servers", this.servers).append("shareChat", this.shareChat).append("announcer", this.announcer).append("dataUnits", this.dataUnits).toString();
    }
}
