package pl.north93.zgame.daemon.servers;

import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.io.File;
import java.util.UUID;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.utils.JavaArguments;
import redis.clients.jedis.Jedis;

public class ServerInstance
{
    private final UUID          serverId;
    private final File          workspace;
    private final JavaArguments java;
    private       boolean       saveLog; // czy log ma zostac zapisany po zatrzymaniu serwera

    public ServerInstance(final UUID serverId, final File workspace, final JavaArguments javaArguments)
    {
        this.serverId = serverId;
        this.workspace = workspace;
        this.java = javaArguments;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    public File getWorkspace()
    {
        return this.workspace;
    }

    public JavaArguments getJava()
    {
        return this.java;
    }

    public boolean isSaveLog()
    {
        return this.saveLog;
    }

    public void setSaveLog(final boolean saveLog)
    {
        this.saveLog = saveLog;
    }

    public ServerImpl getServerInfo()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            return (ServerImpl) API.getMessagePackTemplates().deserialize(Server.class, jedis.get(SERVER.getBytes()));
        }
    }
}
