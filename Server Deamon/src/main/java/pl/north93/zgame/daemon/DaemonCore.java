package pl.north93.zgame.daemon;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import java.net.InetAddress;
import java.net.UnknownHostException;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.DaemonRpc;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.standalone.Launcher;
import pl.north93.zgame.api.standalone.StandaloneApiCore;
import pl.north93.zgame.api.standalone.StandaloneApp;
import pl.north93.zgame.daemon.cfg.DaemonConfig;

public class DaemonCore extends StandaloneApp
{
    private DaemonConfig config;
    private RemoteDaemon daemonInfo;

    @Override
    public String getId()
    {
        return "daemon:" + this.config.daemonName;
    }

    public RemoteDaemon getDaemonInfo()
    {
        return this.daemonInfo;
    }

    @Override
    public void start(final StandaloneApiCore apiCore)
    {
        this.config = loadConfigFile(DaemonConfig.class, API.getFile("daemon.yml"));
        this.daemonInfo = new RemoteDaemon();
        this.daemonInfo.setName(this.config.daemonName);
        try
        {
            this.daemonInfo.setHostName(InetAddress.getLocalHost().getHostName());
        }
        catch (final UnknownHostException e)
        {
            this.daemonInfo.setHostName("<unknown:UnknownHostException>");
        }
        this.daemonInfo.setMaxRam(this.config.maxMemory);
        this.daemonInfo.setRamUsed(0);
        this.daemonInfo.setServerCount(0);
        this.daemonInfo.sendUpdate();
        API.getRpcManager().addRpcImplementation(DaemonRpc.class, new DaemonRpcImpl());
    }

    @Override
    public void stop()
    {
        this.daemonInfo.delete();
    }

    public static void main(final String[] args)
    {
        Launcher.run(DaemonCore.class);
    }
}
