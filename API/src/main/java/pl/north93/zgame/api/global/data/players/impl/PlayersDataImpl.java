package pl.north93.zgame.api.global.data.players.impl;

import java.util.Map;
import java.util.UUID;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
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
                                                .build();
        this.nick2uuid = this.observationManager.cacheBuilder(String.class, UUID.class)
                                                .name("nick2uuid:")
                                                .keyMapper(ObjectKey::new)
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
    public Value<OnlinePlayerImpl> loadPlayer(final UUID uuid, final String name, final Boolean premium, final String proxyId)
    {
        final IOfflinePlayer offlinePlayer = this.getOfflinePlayer(uuid);
        final OnlinePlayerImpl player = new OnlinePlayerImpl();

        player.setUuid(uuid);
        player.setNick(name);
        if (offlinePlayer != null)
        {
            player.transferDataFrom(offlinePlayer);
        }
        else
        {
            player.setLatestNick("");
            player.setGroup(this.permissionsManager.getDefaultGroup());
        }
        player.setServerId(UUID.randomUUID());
        player.setProxyId(proxyId);
        player.setPremium(premium);

        this.nick2uuid.put(name, uuid);
        this.uuid2nick.put(uuid, name);

        return this.observationManager.of(player);
    }

    @Override
    public IOfflinePlayer getOfflinePlayer(final UUID uuid)
    {
        return this.offlinePlayersData.get(uuid);
    }

    private IOfflinePlayer loadOfflinePlayer(final UUID uuid)
    {
        final MongoCollection<Document> playersData = this.storageConnector.getMainDatabase().getCollection("players");
        final Document result = playersData.find(new Document("uuid", uuid)).limit(1).first();

        if (result != null)
        {
            final String latestKnownUsername = result.getString("latestKnownUsername");
            final Group group = this.permissionsManager.getGroupByName(result.getString("group"));

            final MetaStore store = new MetaStore();
            final Map<MetaKey, Object> playerMeta = store.getInternalMap();
            final Document metadata = (Document) result.getOrDefault("metadata", new Document());
            for (final Map.Entry<String, Object> entry : metadata.entrySet())
            {
                playerMeta.put(MetaKey.get(entry.getKey()), entry.getValue());
            }

            return new OfflinePlayerImpl(uuid, latestKnownUsername, group, store);
        }

        return null;
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
        return this.nick2uuid.get(username);
    }

    @Override
    public String uuidToUsername(final UUID playerUuid)
    {
        return this.uuid2nick.get(playerUuid);
    }
}
