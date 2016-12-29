package pl.north93.zgame.skyblock.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.impl.Injector;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.api.utils.Coords3D;

public class IslandDao
{
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector storage;

    public IslandDao()
    {
        Injector.inject(API.getApiCore().getComponentManager(), this);
    }

    public IslandData convert(final Document doc)
    {
        final IslandData data = new IslandData();

        data.setIslandId(doc.get("islandId", UUID.class));
        data.setOwnerId(doc.get("owner", UUID.class));
        data.setServerId(doc.get("server", UUID.class));
        data.setIslandType(doc.getString("type"));
        data.setName(doc.getString("name"));

        final Document islandLocation = doc.get("loc", Document.class);
        data.setIslandLocation(new Coords2D(islandLocation.getInteger("x"), islandLocation.getInteger("z")));

        final Document homeLocation = doc.get("home", Document.class);
        data.setHomeLocation(new Coords3D(homeLocation.getInteger("x"), homeLocation.getInteger("y"), homeLocation.getInteger("z")));

        //noinspection unchecked
        ((List<UUID>) doc.get("members")).forEach(data::addMember);

        return data;
    }

    public Document convert(final IslandData island)
    {
        final Document document = new Document();

        document.put("islandId", island.getIslandId());
        document.put("owner", island.getOwnerId());
        document.put("server", island.getServerId());
        document.put("type", island.getIslandType());
        document.put("name", island.getName());

        final Document islandLocation = new Document();
        islandLocation.put("x", island.getIslandLocation().getX());
        islandLocation.put("z", island.getIslandLocation().getZ());
        document.put("loc", islandLocation);

        final Document homeLocation = new Document();
        homeLocation.put("x", island.getHomeLocation().getX());
        homeLocation.put("y", island.getHomeLocation().getY());
        homeLocation.put("z", island.getHomeLocation().getZ());
        document.put("home", homeLocation);

        document.put("members", island.getMembersUuid());

        return document;
    }

    public void saveIsland(final IslandData islandData)
    {
        final MongoCollection<Document> database = this.storage.getMainDatabase().getCollection("islands");
        database.updateOne(new Document("islandId", islandData.getIslandId()), new Document("$set", this.convert(islandData)), new UpdateOptions().upsert(true));
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

    public IslandData getIsland(final UUID islandId)
    {
        return this.convert(this.storage.getMainDatabase().getCollection("islands").find(new Document("islandId", islandId)).first());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
