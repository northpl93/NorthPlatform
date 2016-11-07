package pl.north93.zgame.daemon;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.DaemonRpc;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.standalone.Launcher;
import pl.north93.zgame.api.standalone.StandaloneApiCore;
import pl.north93.zgame.api.standalone.StandaloneApp;
import pl.north93.zgame.daemon.cfg.DaemonConfig;
import pl.north93.zgame.daemon.servers.ServersManager;

public class DaemonCore extends StandaloneApp
{
    private DaemonConfig   config;
    private RemoteDaemon   daemonInfo;
    private ServersManager serversManager;

    @Override
    public String getId()
    {
        return "daemon:" + this.config.daemonName;
    }

    public RemoteDaemon getDaemonInfo()
    {
        return this.daemonInfo;
    }

    public ServersManager getServersManager()
    {
        return this.serversManager;
    }

    @Override
    public void start(final StandaloneApiCore apiCore)
    {
        this.config = loadConfigFile(DaemonConfig.class, API.getFile("daemon.yml"));
        this.daemonInfo = RemoteDaemon.builder().setName(this.config.daemonName)
                                      .setHostName(apiCore.getHostName())
                                      .setMaxRam(this.config.maxMemory)
                                      .setRamUsed(0)
                                      .setServerCount(0)
                                      .build();
        this.daemonInfo.sendUpdate();
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
