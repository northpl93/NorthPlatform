package pl.arieals.api.minigame.shared.impl.party;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.arieals.api.minigame.shared.api.party.IPartyAccess;
import pl.arieals.api.minigame.shared.api.party.IPartyManager;
import pl.arieals.api.minigame.shared.api.party.PartyInvite;
import pl.arieals.api.minigame.shared.api.party.PlayerAlreadyHasPartyException;
import pl.arieals.api.minigame.shared.api.party.event.PartyNetEvent;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.utils.Wrapper;

public class PartyManagerImpl implements IPartyManager
{
    private static final MetaKey PARTY_INVITE = MetaKey.get("currentPartyInvite", false);
    private static final MetaKey PARTY_META   = MetaKey.get("currentParty", false);
    private final Logger logger = LoggerFactory.getLogger(PartyManagerImpl.class);
    @Inject
    private IObservationManager observer;
    @Inject
    private INetworkManager     networkManager;
    @Inject
    private IEventManager       eventManager;
    private Hash<PartyDataImpl> parties;

    @Bean
    private PartyManagerImpl()
    {
        this.parties = this.observer.getHash(PartyDataImpl.class, "parties");
    }

    @Override
    public IParty getPartyByPlayer(final Identity identity) throws PlayerNotFoundException
    {
        final IPlayersManager players = this.networkManager.getPlayers();

        final UUID playerUuid = players.completeIdentity(identity).getUuid();
        return players.unsafe().getOnlineValue(playerUuid)
                      .flatMap(Value::getOptional)
                      .map(this::getPartyFromPlayer).orElse(null);
    }

    @Override
    public Collection<IParty> getAllParties()
    {
        return Collections.unmodifiableCollection(this.parties.values());
    }

    @Override
    public IParty createParty(final Identity ownerIdentity, final IPlayerStatus location) throws PlayerNotFoundException, PlayerAlreadyHasPartyException
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(ownerIdentity))
        {
            final IPlayer player = t.getPlayer();

            if (this.playerHasParty(player))
            {
                throw new PlayerAlreadyHasPartyException();
            }

            final PartyDataImpl party = new PartyDataImpl(player.getUuid(), location);
            this.setPartyId(player, party.getId());

            // dodajemy party do redisa przed zamknieciem transakcji zeby zmniejszyc ryzyko ze w innym
            // miejscu gracz bedzie mial juz przypisane party którego nie ma jeszcze w redisie...
            this.parties.put(party.getId().toString(), party);

            this.logger.info("Created party with id {}", party.getId());
            return party;
        }
    }

    @Override
    public <T> T access(final UUID partyId, final Function<IPartyAccess, T> atomicFunction)
    {
        final Value<PartyDataImpl> partyValue = this.parties.getAsValue(partyId.toString());

        final Wrapper<T> wrapper = new Wrapper<>();
        partyValue.update(partyData ->
        {
            final PartyAccess partyAccess = new PartyAccess(this, partyData);
            wrapper.set(atomicFunction.apply(partyAccess));

            if (partyAccess.isDeleted())
            {
                this.deleteParty(partyId);
                return null;
            }

            return partyData;
        });

        this.logger.debug("Party data with ID {} has been updated (access)");
        return wrapper.get();
    }

    @Override
    public void access(final UUID partyId, final Consumer<IPartyAccess> atomicFunction)
    {
        this.access(partyId, partyAccess ->
        {
            atomicFunction.accept(partyAccess);
            return null;
        });
    }

    @Override
    public void access(final IPlayer player, final Consumer<IPartyAccess> atomicFunction)
    {
        final IParty party = this.getPartyFromPlayer(player);
        if (party == null)
        {
            return;
        }

        this.access(party.getId(), atomicFunction);
    }

    @Override
    public <T> T access(final IPlayer player, final Function<IPartyAccess, T> atomicFunction)
    {
        final IParty party = this.getPartyFromPlayer(player);
        if (party == null)
        {
            return null;
        }

        return this.access(party.getId(), atomicFunction);
    }

    @Override
    public PartyInvite getLatestInvite(final Identity playerIdentity) throws PlayerNotFoundException
    {
        final IPlayer unsafePlayer = this.networkManager.getPlayers().unsafe().get(playerIdentity).orElseThrow(() -> new PlayerNotFoundException(playerIdentity));
        return this.getLatestInviteFromPlayer(unsafePlayer);
    }

    /*default*/ IPlayerTransaction openTransaction(final Identity ownerIdentity)
    {
        return this.networkManager.getPlayers().transaction(ownerIdentity);
    }

    /*default*/ void callNetEvent(final PartyNetEvent partyNetEvent)
    {
        this.eventManager.callEvent(partyNetEvent);
    }

    @Nullable
    /*default*/ IParty getPartyFromPlayer(final IPlayer player)
    {
        final UUID partyId = player.getMetaStore().get(PARTY_META);
        if (partyId == null)
        {
            return null;
        }

        return this.getParty(partyId);
    }

    @Nullable
    /*default*/ PartyDataImpl getParty(final UUID partyId)
    {
        return this.parties.getAsValue(partyId.toString()).get();
    }

    /*default*/ boolean playerHasParty(final IPlayer player)
    {
        return player.getMetaStore().contains(PARTY_META);
    }

    /*default*/ void setPartyId(final IPlayer player, final UUID partyId)
    {
        final MetaStore metaStore = player.getMetaStore();
        if (partyId == null)
        {
            metaStore.remove(PARTY_META);
            return;
        }
        metaStore.set(PARTY_META, partyId);
    }

    @Nullable
    /*default*/ PartyInvite getLatestInviteFromPlayer(final IPlayer player)
    {
        final PartyInvite invite = player.getMetaStore().get(PARTY_INVITE);
        if (invite == null || invite.isExpired())
        {
            return null;
        }

        return invite;
    }

    /*default*/ void setPartyInvite(final IPlayer player, final PartyInvite invite)
    {
        final MetaStore metaStore = player.getMetaStore();
        if (invite == null)
        {
            metaStore.remove(PARTY_INVITE);
            return;
        }
        metaStore.set(PARTY_INVITE, invite);
    }

    /*default*/ Set<PlayerJoinInfo> getJoinInfos(final Set<UUID> players)
    {
        return players.stream().map(uuid ->
        {
            final Identity identity = Identity.create(uuid, null);
            final IPlayer player = this.networkManager.getPlayers().unsafe().getNullable(identity);

            final boolean isVip = player.getGroup().hasPermission("gamejoin.vip");
            return new PlayerJoinInfo(uuid, isVip, false);
        }).collect(Collectors.toSet());
    }

    private void deleteParty(final UUID partyId)
    {
        this.parties.delete(partyId.toString());
        this.logger.info("Deleted party with ID {}", partyId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
