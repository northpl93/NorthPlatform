package pl.arieals.api.minigame.controller.scaler;

import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.IArena;
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

    private boolean isArenaFree(final IArena arena)
    {
        final GamePhase gamePhase = arena.getGamePhase();

        if (gamePhase == GamePhase.INITIALISING || gamePhase == GamePhase.LOBBY)
        {
            // jesli arena sie uruchamia lub czeka na start to uznajemy ja za wolna
            // to moze nie byc prawda, ale te stany i tak trwaja krotko.
            return true;
        }

        return false;
    }
}
