package pl.arieals.api.minigame.shared.impl.party;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.IPartyManager;
import pl.arieals.api.minigame.shared.api.party.PartyInvite;
import pl.arieals.api.minigame.shared.api.party.PlayerAlreadyHasPartyException;
import pl.arieals.api.minigame.shared.api.party.event.PartyLocationChangeNetEvent;
import pl.arieals.api.minigame.shared.impl.PartyImpl;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class PartyManagerImpl implements IPartyManager
{
    private static final MetaKey PARTY_INVITE = MetaKey.get("currentPartyInvite", false);
    private static final MetaKey PARTY_META   = MetaKey.get("currentParty", false);
    @Inject
    private IObservationManager observer;
    @Inject
    private INetworkManager     networkManager;
    @Inject
    private IEventManager       eventManager;
    private Hash<PartyImpl>     parties;

    @Bean
    private PartyManagerImpl()
    {
        this.parties = this.observer.getHash(PartyImpl.class, "parties");
    }

    private PartyImpl getParty(final UUID partyId)
    {
        return this.parties.get(partyId.toString());
    }

    @Override
    public IParty getPartyByPlayer(final Identity identity) throws PlayerNotFoundException
    {
        final IPlayersManager players = this.networkManager.getPlayers();

        final UUID playerUuid = players.completeIdentity(identity).getUuid();
        return players.unsafe().getOnline(playerUuid)
                      .flatMap(Value::getOptional)
                      .map(this::getPartyByPlayer).orElse(null);
    }

    private IParty getPartyByPlayer(final IPlayer player)
    {
        final UUID partyId = (UUID) player.getMetaStore().get(PARTY_META);
        if (partyId == null)
        {
            return null;
        }

        return this.getParty(partyId);
    }

    private boolean playerHasParty(final IPlayer player)
    {
        return player.getMetaStore().contains(PARTY_META);
    }

    private void setPartyId(final IPlayer player, final UUID partyId)
    {
        player.getMetaStore().set(PARTY_META, partyId);
    }

    @Override
    public IParty createParty(final Identity identity, final INetworkLocation location) throws PlayerNotFoundException, PlayerAlreadyHasPartyException
    {
        final PartyImpl party = new PartyImpl(identity.getUuid(), location);

        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            final IPlayer player = t.getPlayer();

            if (this.playerHasParty(player))
            {
                throw new PlayerAlreadyHasPartyException();
            }

            this.setPartyId(player, party.getId());

            // dodajemy party do redisa przed zamknieciem transakcji zeby zmniejszyc ryzyko ze w innym
            // miejscu gracz bedzie mial juz przypisane party którego nie ma jeszcze w redisie...
            this.parties.put(party.getId().toString(), party);
        }

        return party;
    }

    @Override
    public void changePartyOwner(final UUID partyId, final Identity newOwnerIdentity, final INetworkLocation location)
    {

    }

    @Override
    public void changePartyLocation(final UUID partyId, final INetworkLocation location)
    {
        final Value<PartyImpl> partyValue = this.parties.getAsValue(partyId.toString());
        partyValue.update(partyImpl ->
        {
            partyImpl.setLocation(location);
        });
        this.eventManager.callEvent(new PartyLocationChangeNetEvent(partyValue.get(), location));
    }

    @Override
    public void invitePlayer(final UUID partyId, final Identity identity) throws PlayerNotFoundException, PlayerAlreadyHasPartyException
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            final IPlayer player = t.getPlayer();
            if (t.isOffline())
            {
                throw new PlayerNotFoundException(player.getLatestNick());
            }

            if (this.playerHasParty(player))
            {
                throw new PlayerAlreadyHasPartyException();
            }

            final PartyInvite invite = new PartyInvite(partyId, player.getUuid(), Instant.now(), Duration.ofSeconds(100));
            player.getMetaStore().set(PARTY_INVITE, invite);
        }
    }

    @Override
    public PartyInvite getInvite(final Identity playerIdentity) throws PlayerNotFoundException
    {
        final IPlayer unsafePlayer = this.networkManager.getPlayers().unsafe().get(playerIdentity).orElseThrow(() -> new PlayerNotFoundException(playerIdentity));
        final PartyInvite partyInvite = (PartyInvite) unsafePlayer.getMetaStore().get(PARTY_INVITE);
        if (partyInvite.isExpired())
        {
            return null;
        }

        return partyInvite;
    }

    @Override
    public void addPlayerToParty(final UUID partyId, final Identity identity) throws PlayerAlreadyHasPartyException
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            final IPlayer player = t.getPlayer();

            if (this.playerHasParty(player))
            {
                throw new PlayerAlreadyHasPartyException();
            }

            this.setPartyId(player, partyId);
        }
        catch (final PlayerNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        final Value<PartyImpl> partyValue = this.parties.getAsValue(partyId.toString());
        partyValue.update(party ->
        {
            party.addPlayer(identity.getUuid());
        });
    }

    @Override
    public void deleteParty(final UUID partyId)
    {

    }
}
