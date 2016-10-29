package pl.north93.zgame.api.global.deployment;

import static pl.north93.zgame.api.global.redis.RedisKeys.DAEMON;


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

    public DaemonRpc getRpc()
    {
        return API.getRpcManager().createRpcProxy(DaemonRpc.class, Targets.daemon(this.name));
    }
}
