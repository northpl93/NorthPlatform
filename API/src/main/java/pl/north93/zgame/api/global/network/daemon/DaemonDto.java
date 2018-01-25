package pl.north93.zgame.api.global.network.daemon;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Obiekt przechowujący dane o demonie.
 * Właściwie tylko do celów informacyjnych.
 */
public class DaemonDto
{
    private String  name;
    private String  hostName;
    private Integer maxRam;
    private Integer ramUsed;
    private Integer serverCount;
    private Boolean isAcceptingServers;

    public DaemonDto()
    {
    }

    public DaemonDto(final String name, final String hostName, final Integer maxRam, final Integer ramUsed, final Integer serverCount, final Boolean isAcceptingServers)
    {
        this.name = name;
        this.hostName = hostName;
        this.maxRam = maxRam;
        this.ramUsed = ramUsed;
        this.serverCount = serverCount;
        this.isAcceptingServers = isAcceptingServers;
    }

    public String getName()
    {
        return this.name;
    }

    public String getHostName()
    {
        return this.hostName;
    }

    public Integer getMaxRam()
    {
        return this.maxRam;
    }

    public Integer getRamUsed()
    {
        return this.ramUsed;
    }

    public Integer getServerCount()
    {
        return this.serverCount;
    }

    public Boolean isAcceptingServers()
    {
        return this.isAcceptingServers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("hostName", this.hostName).append("maxRam", this.maxRam).append("ramUsed", this.ramUsed).append("serverCount", this.serverCount).append("isAcceptingServers", this.isAcceptingServers).toString();
    }
}
