package pl.arieals.api.minigame.controller.scaler;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;
import pl.north93.zgame.controller.servers.scaler.value.IScalingValue;

public abstract class AbstractArenasValue implements IScalingValue
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private ArenaManager    arenaManager;

    protected Set<RemoteArena> getArenasOnServers(final Set<UUID> servers)
    {
        return this.arenaManager.getAllArenas().stream().filter(arena -> servers.contains(arena.getServerId())).collect(Collectors.toSet());
    }

    protected Set<UUID> getServerIdInGroup(final LocalManagedServersGroup managedServersGroup)
    {
        return this.networkManager.getServers()
                                  .all()
                                  .stream()
                                  .filter(server -> server.getServersGroup().getName().equals(managedServersGroup.getName()))
                                  .map(Server::getUuid)
                                  .collect(Collectors.toSet());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
