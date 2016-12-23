package pl.north93.zgame.api.global.data.players;

import java.util.Map;
import java.util.UUID;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.NetworkPlayer;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class PlayersDao extends Component
{
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector    storageConnector;
    @InjectComponent("API.MinecraftNetwork.PermissionsManager")
    private PermissionsManager  permissionsManager;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observationManager;

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }

    public Value<NetworkPlayer> loadPlayer(final UUID uuid, final String name, final Boolean premium, final String proxyId)
    {
        final MongoCollection<Document> playersData = this.storageConnector.getMainDatabase().getCollection("players");
        final Document result = playersData.find(new Document("uuid", uuid)).limit(1).first();

        final NetworkPlayer player = new NetworkPlayer();

        player.setUuid(uuid);
        player.setNick(name);
        if (result != null)
        {
            player.setLatestNick(result.getString("latestKnownUsername"));
            player.setGroup(this.permissionsManager.getGroupByName(result.getString("group")));

            final Map<MetaKey, Object> playerMeta = player.getMetaStore().getInternalMap();
            final Document metadata = (Document) result.getOrDefault("metadata", new Document());
            for (final Map.Entry<String, Object> entry : metadata.entrySet())
            {
                playerMeta.put(MetaKey.get(entry.getKey()), entry.getValue());
            }
        }
        else
        {
            player.setLatestNick("");
            player.setGroup(this.permissionsManager.getDefaultGroup());
        }
        player.setServerId(UUID.randomUUID());
        player.setProxyId(proxyId);
        player.setPremium(premium);

        return this.observationManager.of(player);
    }

    public void savePlayer(final NetworkPlayer player)
    {
        final Document playerData = new Document();
        playerData.put("savedAt", System.currentTimeMillis());
        playerData.put("uuid", player.getUuid());
        playerData.put("latestKnownUsername", player.getNick());
        playerData.put("group", player.getGroup().getName());

        final Document metadata = new Document();
        for (final Map.Entry<MetaKey, Object> entry : player.getMetaStore().getInternalMap().entrySet())
        {
            metadata.put(entry.getKey().getKey(), entry.getValue());
        }

        playerData.put("metadata", metadata);

        final MongoCollection<Document> database = this.storageConnector.getMainDatabase().getCollection("players");
        database.updateOne(new Document("uuid", player.getUuid()), new Document("$set", playerData), new UpdateOptions().upsert(true));
    }
}
