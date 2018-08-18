package pl.arieals.globalshops.bungee;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import lombok.extern.slf4j.Slf4j;
import pl.arieals.globalshops.server.impl.PlayerItemInfo;
import pl.arieals.globalshops.server.impl.PlayerShopData;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.impl.players.PersistedPlayer;
import pl.north93.zgame.api.global.storage.StorageConnector;

@Slf4j
public class MongoDbConverter extends NorthCommand
{
    @Inject
    private StorageConnector storageConnector;

    public MongoDbConverter()
    {
        super("performconvertion");
        this.setPermission("ONLYCONSOLE");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        log.info("STARTED CONVERSION");
        final MongoDatabase db = this.storageConnector.getMainDatabase();

        final MongoCollection<Document> legacyPlayers = db.getCollection("OLDplayers");
        final MongoCollection<PersistedPlayer> players = db.getCollection("players").withDocumentClass(PersistedPlayer.class);

        for (final Document document : legacyPlayers.find())
        {
            final Map<String, Object> newMap = new HashMap<>();

            final Document metadata = (Document) document.getOrDefault("metadata", new Document());
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
                newMap.put(entry.getKey(), value);
            }

            final Map<String, Integer> items = (Map<String, Integer>) newMap.getOrDefault("globalShops_bought", new HashMap<String, Integer>());
            final Map<String, Integer> shards = (Map<String, Integer>) newMap.getOrDefault("globalShops_shards", new HashMap<String, Integer>());
            final Map<String, String> active = (Map<String, String>) newMap.getOrDefault("globalShops_active", new HashMap<String, String>());

            if (!items.isEmpty())
            {
                // na shardy w zasadzie mozemy miec wyjebane bo nie bylo jeszcze publicznej opcji dostania skrzynek
                final PlayerShopData playerShopData = new PlayerShopData();
                for (final Map.Entry<String, Integer> entry : items.entrySet())
                {
                    final String encodedKey = entry.getKey();
                    final String[] splitedEncodedKey = StringUtils.split(encodedKey, '$');

                    final PlayerItemInfo itemInfo = playerShopData.getItemInfo(splitedEncodedKey[0], splitedEncodedKey[1]);
                    itemInfo.setBoughtLevel(entry.getValue());
                    itemInfo.setShards(shards.getOrDefault(encodedKey, 0));
                }

                for (final Map.Entry<String, String> entry : active.entrySet())
                {
                    final Map<String, String> activeItems = playerShopData.getActiveItems();
                    activeItems.put(entry.getKey(), entry.getValue());
                }

                newMap.put("shopData", playerShopData);
            }

            final PersistedPlayer.PersistedPlayerBuilder builder = PersistedPlayer.builder();
            builder.uuid(document.get("uuid", UUID.class));
            builder.premium(document.getBoolean("premium", true));
            builder.latestKnownUsername(document.getString("latestKnownUsername"));
            builder.displayName(document.getString("displayName"));
            builder.group(document.getString("group"));
            builder.groupExpireAt(Instant.ofEpochMilli(document.getLong("groupExpireAt")));
            builder.isSavedWhileOnline(document.getBoolean("isSavedWhileOnline"));
            builder.savedAt(Instant.ofEpochMilli(document.get("savedAt", Instant.now().toEpochMilli())));
            builder.metadata(newMap);

            final PersistedPlayer persistedPlayer = builder.build();
            log.info("Saving converted PersistedPlayer: {}", persistedPlayer);

            players.insertOne(persistedPlayer);
        }
    }
}
