package pl.north93.zgame.api.global.deployment;

import static pl.north93.zgame.api.global.redis.RedisKeys.DAEMON;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.redis.messaging.RedisUpdatable;
import pl.north93.zgame.api.global.redis.rpc.Targets;

/**
 * Obiekt przechowujący dane o demonie.
 * Właściwie tylko do celów informacyjnych.
 */
public class RemoteDaemon implements RedisUpdatable
{
    private String  name;
    private String  hostName;
    private Integer maxRam;
    private Integer ramUsed;
    private Integer serverCount;
    private Boolean isAcceptingServers;

    @Override
    public String getRedisKey()
    {
        return DAEMON + this.name;
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

    public DaemonRpc getRpc()
    {
        return API.getRpcManager().createRpcProxy(DaemonRpc.class, Targets.daemon(this.name));
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final RemoteDaemon daemon = new RemoteDaemon();

        public Builder setName(final String name)
        {
            this.daemon.setName(name);
            return this;
        }

        public Builder setHostName(final String hostName)
        {
            this.daemon.setHostName(hostName);
            return this;
        }

        public Builder setMaxRam(final Integer maxRam)
        {
            this.daemon.setMaxRam(maxRam);
            return this;
        }

        public Builder setRamUsed(final Integer ramUsed)
        {
            this.daemon.setRamUsed(ramUsed);
            return this;
        }

        public Builder setServerCount(final Integer serverCount)
        {
            this.daemon.setServerCount(serverCount);
            return this;
        }

        public Builder setAcceptingServers(final Boolean acceptingServers)
        {
            this.daemon.setAcceptingServers(acceptingServers);
            return this;
        }

        public RemoteDaemon build()
        {
            return this.daemon;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("daemon", this.daemon).toString();
        }
    }
}
