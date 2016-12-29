package pl.north93.zgame.daemon;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.DaemonRpc;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.standalone.Launcher;
import pl.north93.zgame.api.standalone.StandaloneApiCore;
import pl.north93.zgame.api.standalone.StandaloneApp;
import pl.north93.zgame.daemon.cfg.DaemonConfig;
import pl.north93.zgame.daemon.servers.ServersManager;

public class DaemonCore extends StandaloneApp
{
    private DaemonConfig        config;
    private Value<RemoteDaemon> daemonInfo;
    private ServersManager      serversManager;

    @Override
    public String getId()
    {
        return "daemon:" + this.config.daemonName;
    }

    public Value<RemoteDaemon> getDaemonInfo()
    {
        return this.daemonInfo;
    }

    public ServersManager getServersManager()
    {
        return this.serversManager;
    }

    @Override
    public void init(final StandaloneApiCore apiCore)
    {
        this.config = loadConfigFile(DaemonConfig.class, API.getFile("daemon.yml"));
    }

    @Override
    public void start(final StandaloneApiCore apiCore)
    {
        final RemoteDaemon daemon = RemoteDaemon.builder().setName(this.config.daemonName)
                                                .setHostName(apiCore.getHostName())
                                                .setMaxRam(this.config.maxMemory)
                                                .setRamUsed(0)
                                                .setServerCount(0)
                                                .setAcceptingServers(true)
                                                .build();

        final IObservationManager observation = apiCore.getComponentManager().getComponent("API.Database.Redis.Observer");
        this.daemonInfo = observation.of(daemon);

        apiCore.getRpcManager().addRpcImplementation(DaemonRpc.class, new DaemonRpcImpl(this));
        this.serversManager = new ServersManager();
        this.serversManager.startServerManager();
    }

    @Override
    public void stop()
    {
        this.daemonInfo.delete();
        this.serversManager.stopServerManager();
    }

    public static void main(final String[] args)
    {
        Launcher.run(DaemonCore.class);
    }
}
