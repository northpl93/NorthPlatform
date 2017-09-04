package pl.north93.zgame.api.global.network.daemon;

import static pl.north93.zgame.api.global.redis.RedisKeys.DAEMON;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.ProvidingRedisKey;

/**
 * Obiekt przechowujący dane o demonie.
 * Właściwie tylko do celów informacyjnych.
 */
public class DaemonDto implements ProvidingRedisKey
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

    @Override
    public ObjectKey getKey()
    {
        return new ObjectKey(DAEMON + this.name);
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getHostName()
    {
        return this.hostName;
    }

    public void setHostName(final String hostName)
    {
        this.hostName = hostName;
    }

    public Integer getMaxRam()
    {
        return this.maxRam;
    }

    public void setMaxRam(final Integer maxRam)
    {
        this.maxRam = maxRam;
    }

    public Integer getRamUsed()
    {
        return this.ramUsed;
    }

    public void setRamUsed(final Integer ramUsed)
    {
        this.ramUsed = ramUsed;
    }

    public Integer getServerCount()
    {
        return this.serverCount;
    }

    public void setServerCount(final Integer serverCount)
    {
        this.serverCount = serverCount;
    }

    public Boolean isAcceptingServers()
    {
        return this.isAcceptingServers;
    }

    public void setAcceptingServers(final Boolean acceptingServers)
    {
        this.isAcceptingServers = acceptingServers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("hostName", this.hostName).append("maxRam", this.maxRam).append("ramUsed", this.ramUsed).append("serverCount", this.serverCount).append("isAcceptingServers", this.isAcceptingServers).toString();
    }
}
