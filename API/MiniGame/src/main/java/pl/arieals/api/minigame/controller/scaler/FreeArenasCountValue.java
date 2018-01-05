package pl.arieals.api.minigame.controller.scaler;

import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;

public class FreeArenasCountValue extends AbstractArenasValue
{
    @Override
    public String getId()
    {
        return "freeArenasCount";
    }

    @Override
    public double calculate(final LocalManagedServersGroup managedServersGroup)
    {
        final Set<UUID> servers = this.getServerIdInGroup(managedServersGroup);
        final Set<RemoteArena> arenasInThisGroup = this.getArenasOnServers(servers);

        return arenasInThisGroup.stream().filter(this::isArenaFree).count();
    }
}
