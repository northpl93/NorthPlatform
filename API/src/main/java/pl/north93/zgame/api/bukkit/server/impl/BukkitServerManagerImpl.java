package pl.north93.zgame.api.bukkit.server.impl;

import java.util.Properties;

import org.bukkit.Bukkit;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.server.IBukkitServerManager;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.impl.ServerImpl;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class BukkitServerManagerImpl extends Component implements IBukkitServerManager
{
    @Inject
    private BukkitApiCore       apiCore;
    @Inject
    private IObservationManager observer;
    // - - - - - - -
    private Value<ServerImpl>   serverValue;

    @Override
    protected void enableComponent()
    {
        this.serverValue = this.observer.get(ServerImpl.class, this.apiCore.getId());
        if (! this.serverValue.isAvailable())
        {
            this.serverValue.set(this.generateServer());
        }
    }

    @Override
    protected void disableComponent()
    {
        this.changeState(ServerState.STOPPING);
    }

    @Override
    public Server getServer()
    {
        return this.serverValue.get();
    }

    @Override
    public void changeState(final ServerState newState)
    {
        this.serverValue.update(server ->
        {
            server.setServerState(newState);
        });
    }

    @Override
    public boolean isShutdownScheduled()
    {
        return false;
    }

    @Override
    public void scheduleShutdown()
    {

    }

    @Override
    public void cancelShutdown()
    {

    }

    private ServerImpl generateServer()
    {
        final Properties properties = System.getProperties();
        return new ServerImpl(this.apiCore.getServerId(), false, ServerType.valueOf(properties.getProperty("northplatform.servertype")), ServerState.STARTING, JoiningPolicy.EVERYONE, Bukkit.getIp(), Bukkit.getPort());
    }
}
