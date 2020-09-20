package pl.north93.northplatform.api.global.network.impl.players;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;


import javax.annotation.Nonnull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.mojang.IMojangCache;
import pl.north93.northplatform.api.global.network.mojang.UsernameDetails;
import pl.north93.northplatform.api.global.network.players.IOfflinePlayer;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.LoginHistoryEntry;
import pl.north93.northplatform.api.global.network.players.NameSizeMistakeException;
import pl.north93.northplatform.api.global.permissions.Group;
import pl.north93.northplatform.api.global.permissions.PermissionsManager;
import pl.north93.northplatform.api.global.redis.RedisKeys;
import pl.north93.northplatform.api.global.redis.observable.Cache;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.ObjectKey;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.global.storage.StorageConnector;

/*default*/ class PlayersDataManager implements IPlayersManager.IPlayersDataManager
{
    @Inject
    private StorageConnector            storageConnector;
    @Inject
    private PermissionsManager          permissionsManager;
    @Inject
    private IObservationManager         observationManager;
    @Inject
    private IMojangCache                mojangCache;
    // // // // // // // // // // // // // // //
    private Cache<UUID, String>         uuid2nick;
    private Cache<String, UUID>         nick2uuid;
    private Cache<UUID, IOfflinePlayer> offlinePlayersData;

    public PlayersDataManager()
    {
        this.uuid2nick = this.observationManager.cacheBuilder(UUID.class, String.class)
                                                .name("uuid2nick:")
                                                .keyMapper(uuid -> new ObjectKey(uuid.toString()))
                                                .provider(this::findNickFromUuid)
                                                .expire(TimeUnit.DAYS.toSeconds(2))
                                                .build();
        this.nick2uuid = this.observationManager.cacheBuilder(String.class, UUID.class)
                                                .name("nick2uuid:")
                                                .keyMapper(ObjectKey::new)
                                                .provider(this::findUuidFromNick)
                                                .expire(TimeUnit.DAYS.toSeconds(2))
                                                .build();
        this.offlinePlayersData = this.observationManager.cacheBuilder(UUID.class, IOfflinePlayer.class)
                                                         .name("offlineplayers:")
                                                         .keyMapper(uuid -> new ObjectKey(uuid.toString()))
                                                         .provider(this::loadOfflinePlayer)
                                                         .build();
    }

    @Override
    public void logPlayerJoin(final UUID uuid, final String nick, final boolean premium, final String ip, final String bungee)
    {
        final LoginHistoryEntry historyEntry = new LoginHistoryEntry(nick, premium, ip, bungee, Instant.now());

        final MongoDatabase mainDatabase = this.storageConnector.getMainDatabase();
        final MongoCollection<LoginHistoryEntry> joinHistory = mainDatabase.getCollection("join_history").withDocumentClass(LoginHistoryEntry.class);

        joinHistory.insertOne(historyEntry);
    }

    @Override
    public Value<OnlinePlayerImpl> loadPlayer(final UUID uuid, final String name, final Boolean premium, final String proxyId) throws NameSizeMistakeException
    {
        final Optional<IOfflinePlayer> offlinePlayer;
        if (premium)
        {
            offlinePlayer = this.getOfflinePlayer(uuid);
        }
        else
        {
            offlinePlayer = this.getOfflinePlayer(name);
            if (offlinePlayer.isPresent() && ! offlinePlayer.get().getLatestNick().equals(name))
            {
                throw new NameSizeMistakeException(offlinePlayer.get().getLatestNick());
            }
        }
        final OnlinePlayerImpl player = new OnlinePlayerImpl();

        player.setUuid(uuid);
        player.setNick(name);
        if (offlinePlayer.isPresent())
        {
            player.transferDataFrom(offlinePlayer.get());

            // check if group is expired
            if (player.isGroupExpired())
            {
                player.setGroup(this.permissionsManager.getDefaultGroup());
                player.setGroupExpireAt(0);
            }
        }
        else
        {
            player.setDisplayName(null);
            player.setLatestNick(name);
            player.setGroupExpireAt(0);
            player.setGroup(this.permissionsManager.getDefaultGroup());
        }
        player.setServerId(UUID.randomUUID());
        player.setProxyId(proxyId);
        player.setPremium(premium);

        this.nick2uuid.put(name.toLowerCase(Locale.ROOT), uuid);
        this.uuid2nick.put(uuid, name);

        final Value<OnlinePlayerImpl> playerValue = this.observationManager.get(OnlinePlayerImpl.class, this.createPlayerKeyName(name));
        playerValue.set(player);

        return playerValue;
    }

    private String createPlayerKeyName(final String name)
    {
        return RedisKeys.PLAYERS + name.toLowerCase(Locale.ROOT);
    }

    @Override
    public Optional<Value<IOfflinePlayer>> getOfflinePlayerValue(final @Nonnull UUID uuid)
    {
        if (uuid == null)
        {
            return Optional.empty();
        }
        return Optional.of(this.offlinePlayersData.getValue(uuid));
    }

    @Override
    public Optional<Value<IOfflinePlayer>> getOfflinePlayerValue(final String nick)
    {
        return this.nick2uuid.get(nick.toLowerCase(Locale.ROOT)).flatMap(this::getOfflinePlayerValue);
    }

    @Override
    public Optional<IOfflinePlayer> getOfflinePlayer(final UUID uuid)
    {
        return this.offlinePlayersData.get(uuid);
    }

    @Override
    public Optional<IOfflinePlayer> getOfflinePlayer(final String nick)
    {
        final Optional<UUID> uuid = this.nick2uuid.get(nick.toLowerCase(Locale.ROOT));
        return uuid.flatMap(this::getOfflinePlayer);
    }

    private IOfflinePlayer loadOfflinePlayer(final UUID uuid) // used by online mode
    {
        final MongoDatabase mainDatabase = this.storageConnector.getMainDatabase();
        final MongoCollection<PersistedPlayer> players = mainDatabase.getCollection("players").withDocumentClass(PersistedPlayer.class);

        final PersistedPlayer persistedPlayer = players.find(new Document("uuid", uuid)).first();
        return this.readOfflinePlayer(persistedPlayer);
    }

    @SuppressWarnings("unchecked")
    private OfflinePlayerImpl readOfflinePlayer(final PersistedPlayer player)
    {
        if (player == null)
        {
            return null;
        }

        final MetaStore metaStore = new MetaStore();
        player.getMetadata().forEach((key, value) ->
        {
            if (value instanceof BasicDBObject)
            {
                metaStore.set(MetaKey.get(key.toString()), new HashMap((Map) value));
            }
            else
            {
                metaStore.set(MetaKey.get(key.toString()), value);
            }
        });

        final Group group = this.permissionsManager.getGroupByName(player.getGroup());
        final long groupExpireAt = player.getGroupExpireAt().toEpochMilli();
        return new OfflinePlayerImpl(player.getUuid(), player.isPremium(), player.getLatestKnownUsername(), player.getDisplayName(), group, groupExpireAt, metaStore);
    }

    @Override
    public void savePlayer(final IPlayer player)
    {
        final PersistedPlayer persistedPlayer = PersistedPlayer.create(player);

        final MongoDatabase mainDatabase = this.storageConnector.getMainDatabase();
        final MongoCollection<PersistedPlayer> players = mainDatabase.getCollection("players").withDocumentClass(PersistedPlayer.class);

        players.replaceOne(new Document("uuid", player.getUuid()), persistedPlayer, new ReplaceOptions().upsert(true));

        if (player instanceof IOnlinePlayer)
        {
            final IOnlinePlayer onlinePlayer = (IOnlinePlayer) player;
            this.offlinePlayersData.put(player.getUuid(), new OfflinePlayerImpl(onlinePlayer));
        }
        else
        {
            this.offlinePlayersData.put(player.getUuid(), (IOfflinePlayer) player);
        }
    }

    public Optional<UUID> usernameToUuid(final String username)
    {
        return this.nick2uuid.get(username.toLowerCase(Locale.ROOT));
    }

    public Optional<String> uuidToUsername(final UUID playerUuid)
    {
        return this.uuid2nick.get(playerUuid);
    }

    private UUID findUuidFromNick(final String nick)
    {
        final Optional<UsernameDetails> usernameDetails = this.mojangCache.lookupUsernameInLocalDatabase(nick);
        if (usernameDetails.isPresent())
        {
            final UsernameDetails details = usernameDetails.get();
            if (details.isPremium())
            {
                return details.getUuid();
            }
        }
        return this.findUuidFromPlayersDb(nick);
    }

    private UUID findUuidFromPlayersDb(final String nick)
    {
        final MongoCollection<Document> playersData = this.storageConnector.getMainDatabase().getCollection("players");
        final Pattern nickPattern = compile('^' + Pattern.quote(nick) + '$', CASE_INSENSITIVE);
        final Document result = playersData.find(new Document("latestKnownUsername", nickPattern)).limit(1).first();
        return result == null ? null : result.get("uuid", UUID.class);
    }

    private String findNickFromUuid(final UUID uuid)
    {
        final MongoCollection<Document> playersData = this.storageConnector.getMainDatabase().getCollection("players");
        final Document result = playersData.find(new Document("uuid", uuid)).limit(1).first();
        if (result != null)
        {
            return result.getString("latestKnownUsername");
        }
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
