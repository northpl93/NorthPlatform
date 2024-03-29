package pl.north93.northplatform.api.minigame.shared.impl.party;

import static java.util.Optional.ofNullable;


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

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.minigame.shared.api.PlayerJoinInfo;
import pl.north93.northplatform.api.minigame.shared.api.party.IParty;
import pl.north93.northplatform.api.minigame.shared.api.party.IPartyAccess;
import pl.north93.northplatform.api.minigame.shared.api.party.IPartyManager;
import pl.north93.northplatform.api.minigame.shared.api.party.PartyInvite;
import pl.north93.northplatform.api.minigame.shared.api.party.PlayerAlreadyHasPartyException;
import pl.north93.northplatform.api.minigame.shared.api.party.event.PartyNetEvent;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.players.PlayerNotFoundException;
import pl.north93.northplatform.api.global.redis.event.IEventManager;
import pl.north93.northplatform.api.global.redis.observable.Hash;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.global.utils.Wrapper;

@Slf4j
public class PartyManagerImpl implements IPartyManager
{
    private static final MetaKey PARTY_INVITE = MetaKey.get("currentPartyInvite");
    private static final MetaKey PARTY_META   = MetaKey.get("currentParty");
    @Inject
    private IObservationManager observer;
    @Inject
    private IPlayersManager     playersManager;
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
        final IPlayer player = this.playersManager.unsafe().getNullable(identity);

        return ofNullable(player).map(this::getPartyFromPlayer).orElse(null);
    }

    @Override
    public Collection<IParty> getAllParties()
    {
        return Collections.unmodifiableCollection(this.parties.values());
    }

    @Override
    public IParty createParty(final Identity ownerIdentity, final IPlayerStatus location) throws PlayerNotFoundException, PlayerAlreadyHasPartyException
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(ownerIdentity))
        {
            final IPlayer player = t.getPlayer();

            if (this.playerHasParty(player))
            {
                throw new PlayerAlreadyHasPartyException();
            }

            final PartyDataImpl party = new PartyDataImpl(player.getIdentity(), location);
            this.setPartyId(player, party.getId());

            // dodajemy party do redisa przed zamknieciem transakcji zeby zmniejszyc ryzyko ze w innym
            // miejscu gracz bedzie mial juz przypisane party którego nie ma jeszcze w redisie...
            this.parties.put(party.getId().toString(), party);

            log.info("Created party with id {} and owner {}", party.getId(), party.getOwner());
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

        log.debug("Party data with ID {} has been updated (access)", partyId);
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
        final IPlayer unsafePlayer = this.playersManager.unsafe().get(playerIdentity).orElseThrow(() -> new PlayerNotFoundException(playerIdentity));
        return this.getLatestInviteFromPlayer(unsafePlayer);
    }

    /*default*/ IPlayerTransaction openTransaction(final Identity ownerIdentity)
    {
        return this.playersManager.transaction(ownerIdentity);
    }

    /*default*/ void callNetEvent(final PartyNetEvent partyNetEvent)
    {
        this.eventManager.callEvent(partyNetEvent);
    }

    @Nullable
    /*default*/ IParty getPartyFromPlayer(final IPlayer player)
    {
        final UUID partyId = player.getOnlineMetaStore().get(PARTY_META);
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
        return player.getOnlineMetaStore().contains(PARTY_META);
    }

    /*default*/ void setPartyId(final IPlayer player, final UUID partyId)
    {
        final MetaStore metaStore = player.getOnlineMetaStore();
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
        final PartyInvite invite = player.getOnlineMetaStore().get(PARTY_INVITE);
        if (invite == null || invite.isExpired())
        {
            return null;
        }

        return invite;
    }

    /*default*/ void setPartyInvite(final IPlayer player, final PartyInvite invite)
    {
        final MetaStore metaStore = player.getOnlineMetaStore();
        if (invite == null)
        {
            metaStore.remove(PARTY_INVITE);
            return;
        }
        metaStore.set(PARTY_INVITE, invite);
    }

    /*default*/ Set<PlayerJoinInfo> getJoinInfos(final Set<Identity> players)
    {
        return players.stream().map(identity ->
        {
            final IPlayer player = this.playersManager.unsafe().getNullable(identity);

            final boolean isVip = player.getGroup().hasPermission("gamejoin.vip");
            return new PlayerJoinInfo(player.getUuid(), isVip, false);
        }).collect(Collectors.toSet());
    }

    private void deleteParty(final UUID partyId)
    {
        this.parties.delete(partyId.toString());
        log.info("Deleted party with ID {}", partyId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
