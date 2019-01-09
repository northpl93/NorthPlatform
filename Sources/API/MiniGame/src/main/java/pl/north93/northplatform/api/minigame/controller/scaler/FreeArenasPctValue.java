package pl.north93.northplatform.api.minigame.controller.scaler;

import java.util.Set;
import java.util.UUID;

import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.controller.servers.groups.LocalManagedServersGroup;

public class FreeArenasPctValue extends AbstractArenasValue
{
    @Override
    public String getId()
    {
        return "freeArenasPct";
    }

    @Override
    public double calculate(final LocalManagedServersGroup managedServersGroup)
    {
        final Set<UUID> servers = this.getServerIdInGroup(managedServersGroup);
        final Set<RemoteArena> arenasInThisGroup = this.getArenasOnServers(servers);

        final double totalArenas = arenasInThisGroup.size();
        final double freeArenas = arenasInThisGroup.stream().filter(this::isArenaFree).count();

        return (freeArenas / totalArenas) * 100D;
    }
}
