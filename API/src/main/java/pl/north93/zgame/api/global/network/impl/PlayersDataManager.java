package pl.north93.zgame.api.global.network.impl;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.NameSizeMistakeException;
import pl.north93.zgame.api.global.network.players.UsernameDetails;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.storage.StorageConnector;

class PlayersDataManager implements IPlayersManager.IPlayersDataManager
{
    private PlayersManagerImpl          playersManager;
    @Inject
    private StorageConnector            storageConnector;
    @Inject
    private PermissionsManager          permissionsManager;
    @Inject
    private IObservationManager         observationManager;
    // // // // // // // // // // // // // // //
    private Cache<UUID, String>         uuid2nick;
    private Cache<String, UUID>         nick2uuid;
    private Cache<UUID, IOfflinePlayer> offlinePlayersData;

    public PlayersDataManager(final PlayersManagerImpl playersManager)
    {
        this.playersManager = playersManager;
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
        final MongoCollection<Document> history = this.storageConnector.getMainDatabase().getCollection("join_history");
        final Document doc = new Document("uuid", uuid);
        doc.put("nick", nick);
        doc.put("premium", premium);
        doc.put("ip", ip);
        doc.put("bungee", bungee);
        doc.put("at", System.currentTimeMillis());
        history.insertOne(doc);
    }

    @Override
    public Value<OnlinePlayerImpl> loadPlayer(final UUID uuid, final String name, final Boolean premium, final String proxyId) throws NameSizeMistakeException
    {
        final IOfflinePlayer offlinePlayer;
        if (premium)
        {
            offlinePlayer = this.getOfflinePlayer(uuid);
        }
        else
        {
            offlinePlayer = this.getOfflinePlayer(name);
            if (offlinePlayer != null && ! offlinePlayer.getLatestNick().equals(name))
            {
                throw new NameSizeMistakeException(offlinePlayer.getLatestNick());
            }
        }
        final OnlinePlayerImpl player = new OnlinePlayerImpl();

        player.setUuid(uuid);
        player.setNick(name);
        if (offlinePlayer != null)
        {
            player.transferDataFrom(offlinePlayer);

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
            player.setBanned(false);
        }
        player.setServerId(UUID.randomUUID());
        player.setProxyId(proxyId);
        player.setPremium(premium);

        this.nick2uuid.put(name.toLowerCase(Locale.ENGLISH), uuid);
        this.uuid2nick.put(uuid, name);

        return this.observationManager.of(player);
    }

    @Override
    public Value<IOfflinePlayer> getOfflinePlayerValue(final UUID uuid)
    {
        if (uuid == null)
        {
            return null;
        }
        return this.offlinePlayersData.getValue(uuid);
    }

    @Override
    public Value<IOfflinePlayer> getOfflinePlayerValue(final String nick)
    {
        final UUID uuid = this.nick2uuid.get(nick.toLowerCase(Locale.ENGLISH));
        if (uuid == null)
        {
            return null;
        }
        return this.getOfflinePlayerValue(uuid);
    }

    @Override
    public IOfflinePlayer getOfflinePlayer(final UUID uuid)
    {
        return this.offlinePlayersData.get(uuid);
    }

    @Override
    public IOfflinePlayer getOfflinePlayer(final String nick)
    {
        final UUID uuid = this.nick2uuid.get(nick.toLowerCase(Locale.ENGLISH));
        if (uuid == null)
        {
            return null;
        }
        return this.getOfflinePlayer(uuid);
    }

    private IOfflinePlayer loadOfflinePlayer(final UUID uuid) // used by online mode
    {
        final MongoCollection<Document> playersData = this.storageConnector.getMainDatabase().getCollection("players");
        final Document result = playersData.find(new Document("uuid", uuid)).limit(1).first();
        return this.readOfflinePlayer(result);
    }

    private OfflinePlayerImpl readOfflinePlayer(final Document result)
    {
        if (result == null)
        {
            return null;
        }

        final String latestKnownUsername = result.getString("latestKnownUsername");
        final String displayName = result.containsKey("displayName") ? result.getString("displayName") : ""; // gdy nie ma niestandardowej nazwy to nie ma fielda
        final boolean isBanned = result.getBoolean("banned");
        final Group group = this.permissionsManager.getGroupByName(result.getString("group"));
        final long groupExpireAt = result.getLong("groupExpireAt");

        final MetaStore store = new MetaStore();
        final Map<MetaKey, Object> playerMeta = store.getInternalMap();
        final Document metadata = (Document) result.getOrDefault("metadata", new Document());
        for (final Map.Entry<String, Object> entry : metadata.entrySet())
        {
            final Object value;
            if (entry.getValue() instanceof Document)
            {
                value = new HashMap<>((Document) entry.getValue());
            }
            else
            {
                value = entry.getValue();
            }
            playerMeta.put(MetaKey.get(entry.getKey()), value);
        }

        return new OfflinePlayerImpl(result.get("uuid", UUID.class), latestKnownUsername, displayName, isBanned, group, groupExpireAt, store);
    }

    @Override
    public void savePlayer(final IPlayer player)
    {
        final Document playerData = new Document();

        playerData.put("savedAt", System.currentTimeMillis());
        playerData.put("uuid", player.getUuid());
        playerData.put("banned", player.isBanned());
        playerData.put("group", player.getGroup().getName());
        playerData.put("groupExpireAt", player.getGroupExpireAt());

        if (player.hasDisplayName())
        {
            playerData.put("displayName", player.getDisplayName());
        }

        final Document metadata = new Document();
        for (final Map.Entry<MetaKey, Object> entry : player.getMetaStore().getInternalMap().entrySet())
        {
            final MetaKey metaKey = entry.getKey();
            if (metaKey.isPersist())
            {
                // save to database only when key is marked as persist.
                metadata.put(metaKey.getKey(), entry.getValue());
            }
        }
        playerData.put("metadata", metadata);

        if (player instanceof IOnlinePlayer)
        {
            final IOnlinePlayer onlinePlayer = (IOnlinePlayer) player;
            playerData.put("latestKnownUsername", onlinePlayer.getNick());
            playerData.put("isSavedWhileOnline", true);

            this.offlinePlayersData.put(player.getUuid(), new OfflinePlayerImpl(onlinePlayer));
        }
        else if (player instanceof IOfflinePlayer)
        {
            playerData.put("isSavedWhileOnline", false);
            this.offlinePlayersData.put(player.getUuid(), (IOfflinePlayer) player);
        }

        final MongoCollection<Document> database = this.storageConnector.getMainDatabase().getCollection("players");
        database.updateOne(new Document("uuid", player.getUuid()), new Document("$set", playerData), new UpdateOptions().upsert(true));
    }

    public UUID usernameToUuid(final String username)
    {
        return this.nick2uuid.get(username.toLowerCase(Locale.ENGLISH));
    }

    public String uuidToUsername(final UUID playerUuid)
    {
        return this.uuid2nick.get(playerUuid);
    }

    private UUID findUuidFromNick(final String nick)
    {
        final Optional<UsernameDetails> usernameDetails = this.playersManager.getCache().getNickDetails(nick);
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
