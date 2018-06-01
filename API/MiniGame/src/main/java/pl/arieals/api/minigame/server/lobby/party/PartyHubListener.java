package pl.arieals.api.minigame.server.lobby.party;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.server.lobby.hub.SelectHubServerJoinAction;
import pl.arieals.api.minigame.server.utils.party.PartyClient;
import pl.arieals.api.minigame.shared.api.status.InHubStatus;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.event.LocationChangePartyNetEvent;
import pl.north93.zgame.api.bukkit.player.IBukkitPlayers;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;

public class PartyHubListener implements AutoListener
{
    @Inject
    private MiniGameServer  miniGameServer;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IBukkitPlayers  bukkitPlayers;
    @Inject
    private IBukkitExecutor bukkitExecutor;
    @Inject
    private PartyClient     partyClient;

    @EventHandler
    public void reportPartyLocationChangeWhenHubSwitched(final PlayerSwitchedHubEvent event)
    {
        final Player player = event.getPlayer();

        final IParty party = this.partyClient.getPlayerParty(player);
        if (party == null || ! party.isOwner(player.getUniqueId()))
        {
            // gracz nie ma party lub nie moze decydowac o zmianie lokacji
            return;
        }

        final LobbyManager lobbyManager = this.miniGameServer.getServerManager();
        this.partyClient.changePartyLocation(party, lobbyManager.getLocation(player));
    }

    @NetEventSubscriber(LocationChangePartyNetEvent.class)
    public void teleportPartyToNewHubLocation(final LocationChangePartyNetEvent event)
    {
        final IPlayerStatus newLocation = event.getLocation();
        if (newLocation.getType() != IPlayerStatus.StatusType.HUB)
        {
            // kiedy nowa lokalizacja znajduje sie na serwerze gier to nic nie robimy
            // bo tu to nas nie obchodzi
            return;
        }

        final UUID newServerId = newLocation.getServerId();
        final String newHubId = ((InHubStatus) newLocation).getHubId();

        final LobbyManager lobbyManager = this.miniGameServer.getServerManager();

        final IParty party = event.getParty();
        for (final UUID playerId : party.getPlayers())
        {
            final INorthPlayer localPlayer = this.bukkitPlayers.getPlayer(playerId);
            if (localPlayer == null)
            {
                // jak nie mamy takiego gracza lokalnie online to nic nie robimy
                continue;
            }

            // sprawdzamy czy gracz juz znajduje sie na dobrym serwerze, czy trzeba go przeniesc
            if (newServerId.equals(lobbyManager.getServerId()))
            {
                final IPlayerStatus playerLocation = lobbyManager.getLocation(localPlayer);
                if (newLocation.equals(playerLocation))
                {
                    // gracz juz znajduje sie w tej lokalizacji, nic nie robimy
                    continue;
                }

                // synchronizujemy sie do glownego watku serwera i zmieniamy lokalnie huba gracza
                this.bukkitExecutor.sync(() -> lobbyManager.tpToHub(Collections.singleton(localPlayer), newHubId));
            }
            else
            {
                // trzeba przeniesc gracza na inny serwer z hubami, wiec to robimy
                final Server newServer = this.networkManager.getServers().withUuid(newServerId);
                localPlayer.connectTo(newServer, new SelectHubServerJoinAction(newHubId));
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
