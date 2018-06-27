package pl.arieals.api.minigame.controller.scaler;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.arena.ArenaManager;
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

    // sprawdza czy dana arena jest wolna, czyli zdatna do wejscia dla graczy.
    protected boolean isArenaFree(final IArena arena)
    {
        final GamePhase gamePhase = arena.getGamePhase();

        if (gamePhase == GamePhase.INITIALISING || gamePhase == GamePhase.LOBBY)
        {
            // jesli arena sie uruchamia lub czeka na start to uznajemy ja za wolna
            // to moze nie byc prawda, ale te stany i tak trwaja krotko.
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (arena.isDynamic() && gamePhase == GamePhase.STARTED)
        {
            // jesli arena jest dynamiczna i gra jest w trakcie to sprawdzamy ilosc graczy
            // jak sa wolne sloty to takze uznajemy arene za wolna
            return arena.getMaxPlayers() > arena.getPlayersCount();
        }

        return false;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
