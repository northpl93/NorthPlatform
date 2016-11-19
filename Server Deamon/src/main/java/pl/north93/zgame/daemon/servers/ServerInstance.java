package pl.north93.zgame.daemon.servers;

import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.api.global.utils.JavaArguments;
import redis.clients.jedis.Jedis;

public class ServerInstance
{
    private final ServersManager serversManager;
    private final UUID           serverId;
    private final File           workspace;
    private final JavaArguments  java;
    private       ServerConsole  serverConsole;
    private       boolean        saveLog; // czy log ma zostac zapisany po zatrzymaniu serwera

    public ServerInstance(final ServersManager serversManager, final UUID serverId, final File workspace, final JavaArguments javaArguments)
    {
        this.serversManager = serversManager;
        this.serverId = serverId;
        this.workspace = workspace;
        this.java = javaArguments;
    }

    public ServersManager getServersManager()
    {
        return this.serversManager;
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

    public ServerConsole getServerConsole()
    {
        return this.serverConsole;
    }

    /*default*/ void setServerConsole(final ServerConsole newConsole)
    {
        API.debug("Server console of " + this.serverId + " is now " + newConsole);
        this.serverConsole = newConsole;
    }

    public void executeCommand(final String consoleCommand)
    {
        if (this.serverConsole == null)
        {
            API.getLogger().warning("Tried to execute command '" + consoleCommand + "' on server with ID " + this.serverId + " but serverConsole was null.");
            return;
        }
        this.serverConsole.executeCommand(consoleCommand);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverId", this.serverId).append("workspace", this.workspace).append("java", this.java).append("serverConsole", this.serverConsole).append("saveLog", this.saveLog).toString();
    }
}
