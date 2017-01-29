package pl.north93.zgame.timegroup.shared;

import java.util.UUID;

import com.mongodb.client.MongoCollection;

import org.bson.Document;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.network.INetworkManager;

public class GroupsManager
{
    @InjectComponent("")
    private StorageConnector storage;
    @InjectComponent("")
    private INetworkManager  networkManager;

    public GroupExpireInfo getExpireInfo(final UUID playerId)
    {
        final MongoCollection<Document> groups = this.storage.getMainDatabase().getCollection("groups_expire");
        final Document document = groups.find(new Document("uuid", playerId)).first();
        if (document == null)
        {
            return null;
        }

        return this.convert(document);
    }

    private Document convert(final GroupExpireInfo group)
    {
        final Document doc = new Document();

        doc.put("uuid", group.getPlayerId());
        doc.put("group", group.getGroup());
        doc.put("expiretime", group.getExpireAt());
        doc.put("givetime", group.getGivenAt());

        return doc;
    }

    private GroupExpireInfo convert(final Document doc)
    {
        return null;
    }
}
