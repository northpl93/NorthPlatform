package pl.north93.northplatform.api.minigame.server.lobby.party;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.IBukkitPlayers;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.server.lobby.hub.SelectHubServerJoinAction;
import pl.north93.northplatform.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.north93.northplatform.api.minigame.server.shared.party.PartyClient;
import pl.north93.northplatform.api.minigame.server.shared.status.IPlayerStatusProvider;
import pl.north93.northplatform.api.minigame.shared.api.party.IParty;
import pl.north93.northplatform.api.minigame.shared.api.party.event.LocationChangePartyNetEvent;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.northplatform.api.minigame.shared.api.status.InHubStatus;

public class PartyHubListener implements AutoListener
{
    @Inject
    private LobbyManager lobbyManager;
    @Inject
    private IPlayerStatusProvider playerStatusProvider;
    @Inject
    private IServersManager serversManager;
    @Inject
    private IBukkitPlayers bukkitPlayers;
    @Inject
    private IBukkitExecutor bukkitExecutor;
    @Inject
    private PartyClient partyClient;

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

        this.partyClient.changePartyLocation(party, this.playerStatusProvider.getLocation(player));
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

        final IParty party = event.getParty();
        for (final Identity identity : party.getPlayers())
        {
            final INorthPlayer localPlayer = this.bukkitPlayers.getPlayer(identity.getUuid());
            if (localPlayer == null)
            {
                // jak nie mamy takiego gracza lokalnie online to nic nie robimy
                continue;
            }

            // sprawdzamy czy gracz juz znajduje sie na dobrym serwerze, czy trzeba go przeniesc
            if (newServerId.equals(this.lobbyManager.getServerId()))
            {
                final IPlayerStatus playerLocation = this.playerStatusProvider.getLocation(localPlayer);
                if (newLocation.equals(playerLocation))
                {
                    // gracz juz znajduje sie w tej lokalizacji, nic nie robimy
                    continue;
                }

                // synchronizujemy sie do glownego watku serwera i zmieniamy lokalnie huba gracza
                this.bukkitExecutor.sync(() -> this.lobbyManager.tpToHub(Collections.singleton(localPlayer), newHubId));
            }
            else
            {
                // trzeba przeniesc gracza na inny serwer z hubami, wiec to robimy
                final Server newServer = this.serversManager.withUuid(newServerId);
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
