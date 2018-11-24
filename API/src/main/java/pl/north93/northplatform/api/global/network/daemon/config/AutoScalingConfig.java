package pl.north93.northplatform.api.global.network.daemon.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import java.util.List;

@XmlRootElement(name = "autoscaler")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ManagedServersGroupConfig.class, UnManagedServersGroupConfig.class})
public class AutoScalingConfig
{
    @XmlElement(name = "pattern")
    @XmlElementWrapper(name = "patterns")
    private List<ServerPatternConfig> patterns;
    @XmlAnyElement(lax = true)
    @XmlElementWrapper(name = "groups")
    private List<ServersGroupConfig> serversGroups;

    public List<ServerPatternConfig> getPatterns()
    {
        return this.patterns;
    }

    public List<ServersGroupConfig> getServersGroups()
    {
        return this.serversGroups;
    }
}
