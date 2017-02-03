package pl.north93.zgame.skyblock.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.utils.Coords2D;

public class IslandDao
{
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector        storage;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager     observer;
    private Cache<UUID, IslandData> islandDataCache;

    @PostInject
    private void init()
    {
        this.islandDataCache = this.observer.cacheBuilder(UUID.class, IslandData.class)
                                            .name("isldata:")
                                            .keyMapper(uuid -> new ObjectKey(uuid.toString()))
                                            .provider(this::getIslandFromDatabase)
                                            .build();
    }

    public void modifyIsland(final UUID islandId, final Consumer<IslandData> modifier)
    {
        this.islandDataCache.getValue(islandId).update(data ->
        {
            modifier.accept(data);
            this.saveIsland(data);
        });
    }

    public void saveIsland(final IslandData islandData)
    {
        final MongoCollection<Document> database = this.storage.getMainDatabase().getCollection("islands");
        database.updateOne(new Document("islandId", islandData.getIslandId()), new Document("$set", this.convert(islandData)), new UpdateOptions().upsert(true));
        this.islandDataCache.put(islandData.getIslandId(), islandData);
    }

    public List<IslandData> getAllIslands()
    {
        final MongoCollection<Document> database = this.storage.getMainDatabase().getCollection("islands");

        final ArrayList<IslandData> tempList = new ArrayList<>();
        for (final Document document : database.find())
        {
            tempList.add(this.convert(document));
        }

        return tempList;
    }

    public List<IslandData> getAllIslands(final UUID serverId, final String islandType)
    {
        final MongoCollection<Document> database = this.storage.getMainDatabase().getCollection("islands");

        final Document criteria = new Document();
        criteria.put("server", serverId);
        criteria.put("type", islandType);

        final ArrayList<IslandData> tempList = new ArrayList<>();
        for (final Document document : database.find(criteria))
        {
            tempList.add(this.convert(document));
        }

        return tempList;
    }

    public Value<IslandData> getIslandValue(final UUID islandId)
    {
        return this.islandDataCache.getValue(islandId);
    }

    public IslandData getIsland(final UUID islandId)
    {
        if (islandId == null)
        {
            API.getLogger().warning("Somebody invoked IslandDao#getIsland(null)");
            return null;
        }
        return this.islandDataCache.get(islandId);
    }

    public void deleteIsland(final UUID islandId)
    {
        this.storage.getMainDatabase().getCollection("islands").deleteOne(new Document("islandId", islandId));
        this.islandDataCache.getValue(islandId).delete();
    }

    private IslandData getIslandFromDatabase(final UUID islandId) // used by Cache as provider
    {
        return this.convert(this.storage.getMainDatabase().getCollection("islands").find(new Document("islandId", islandId)).first());
    }

    private IslandData convert(final Document doc)
    {
        if (doc == null)
        {
            return null;
        }
        final IslandData data = new IslandData();

        data.setIslandId(doc.get("islandId", UUID.class));
        data.setOwnerId(doc.get("owner", UUID.class));
        data.setServerId(doc.get("server", UUID.class));
        data.setIslandType(doc.getString("type"));
        data.setName(doc.getString("name"));
        data.setAcceptingVisits(doc.getBoolean("visits"));
        data.setBiome(NorthBiome.valueOf(doc.getString("biome")));

        final Document islandLocation = doc.get("loc", Document.class);
        data.setIslandLocation(new Coords2D(islandLocation.getInteger("x"), islandLocation.getInteger("z")));

        final Document homeLoc = doc.get("home", Document.class);
        data.setHomeLocation(new HomeLocation(homeLoc.getDouble("x"), homeLoc.getDouble("y"), homeLoc.getDouble("z"), new Float(homeLoc.getDouble("yaw")), new Float(homeLoc.getDouble("pitch"))));

        //noinspection unchecked
        ((List<UUID>) doc.get("members")).forEach(data::addMember);
        //noinspection unchecked
        data.getInvitations().addAll((List<UUID>) doc.get("invitations"));

        return data;
    }

    private Document convert(final IslandData island)
    {
        final Document document = new Document();

        document.put("islandId", island.getIslandId());
        document.put("owner", island.getOwnerId());
        document.put("server", island.getServerId());
        document.put("type", island.getIslandType());
        document.put("name", island.getName());
        document.put("visits", island.getAcceptingVisits());
        document.put("biome", island.getBiome().toString());

        final Document islandLocation = new Document();
        islandLocation.put("x", island.getIslandLocation().getX());
        islandLocation.put("z", island.getIslandLocation().getZ());
        document.put("loc", islandLocation);

        final Document homeLocation = new Document();
        homeLocation.put("x", island.getHomeLocation().getX());
        homeLocation.put("y", island.getHomeLocation().getY());
        homeLocation.put("z", island.getHomeLocation().getZ());
        homeLocation.put("yaw", island.getHomeLocation().getYaw());
        homeLocation.put("pitch", island.getHomeLocation().getPitch());
        document.put("home", homeLocation);

        document.put("invitations", island.getInvitations());
        document.put("members", island.getMembersUuid());

        return document;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
