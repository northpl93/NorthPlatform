package pl.north93.northplatform.api.minigame.controller.scaler;

import java.util.Set;
import java.util.UUID;

import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.controller.servers.groups.LocalManagedServersGroup;

public class FreeArenasSlotsPctValue extends AbstractArenasValue
{
    @Override
    public String getId()
    {
        return "freeArenasSlotsPct";
    }

    @Override
    public double calculate(final LocalManagedServersGroup managedServersGroup)
    {
        final Set<UUID> servers = this.getServerIdInGroup(managedServersGroup);
        final Set<RemoteArena> arenasInThisGroup = this.getArenasOnServers(servers);

        final double maxPlayers = arenasInThisGroup.stream().mapToInt(IArena::getMaxPlayers).sum();
        final int currentPlayers = arenasInThisGroup.stream().mapToInt(IArena::getPlayersCount).sum();
        final double freeSlots = maxPlayers - currentPlayers;

        return (freeSlots / maxPlayers) * 100D;
    }
}
