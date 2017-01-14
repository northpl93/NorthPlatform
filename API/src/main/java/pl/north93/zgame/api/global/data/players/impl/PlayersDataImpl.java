package pl.north93.zgame.api.global.data.players.impl;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;


import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.data.UsernameCache;
import pl.north93.zgame.api.global.data.players.IPlayersData;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.IOfflinePlayer;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.network.IPlayer;
import pl.north93.zgame.api.global.network.impl.OfflinePlayerImpl;
import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.Value;

public class PlayersDataImpl extends Component implements IPlayersData
{
    @InjectComponent("API.MinecraftNetwork.UsernameCache")
    private UsernameCache       usernameCache;
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector    storageConnector;
    @InjectComponent("API.MinecraftNetwork.PermissionsManager")
    private PermissionsManager  permissionsManager;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observationManager;
    // // // // // // // // // // // // // // //
    private Cache<UUID, String>         uuid2nick;
    private Cache<String, UUID>         nick2uuid;
    private Cache<UUID, IOfflinePlayer> offlinePlayersData;

    @Override
    protected void enableComponent()
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
    protected void disableComponent()
    {
        if (this.getApiCore().getId().equals("controller"))
        {
            this.uuid2nick.clear();
            this.nick2uuid.clear();
        }
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
        }
        else
        {
            player.setLatestNick(name);
            player.setGroup(this.permissionsManager.getDefaultGroup());
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
        final Group group = this.permissionsManager.getGroupByName(result.getString("group"));

        final MetaStore store = new MetaStore();
        final Map<MetaKey, Object> playerMeta = store.getInternalMap();
        final Document metadata = (Document) result.getOrDefault("metadata", new Document());
        for (final Map.Entry<String, Object> entry : metadata.entrySet())
        {
            playerMeta.put(MetaKey.get(entry.getKey()), entry.getValue());
        }

        return new OfflinePlayerImpl(result.get("uuid", UUID.class), latestKnownUsername, group, store);
    }

    @Override
    public void savePlayer(final IPlayer player)
    {
        final Document playerData = new Document();

        playerData.put("savedAt", System.currentTimeMillis());
        playerData.put("uuid", player.getUuid());
        playerData.put("group", player.getGroup().getName());

        final Document metadata = new Document();
        for (final Map.Entry<MetaKey, Object> entry : player.getMetaStore().getInternalMap().entrySet())
        {
            metadata.put(entry.getKey().getKey(), entry.getValue());
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

    @Override
    public UUID usernameToUuid(final String username)
    {
        return this.nick2uuid.get(username.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String uuidToUsername(final UUID playerUuid)
    {
        return this.uuid2nick.get(playerUuid);
    }

    private UUID findUuidFromNick(final String nick)
    {
        final Optional<UsernameCache.UsernameDetails> usernameDetails = this.usernameCache.getUsernameDetails(nick);
        if (usernameDetails.isPresent())
        {
            final UsernameCache.UsernameDetails details = usernameDetails.get();
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
}
