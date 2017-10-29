package pl.north93.zgame.datashare.sharedimpl;

import java.util.UUID;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.storage.StorageConnector;

public class ShareDataDao
{
    @Inject
    private StorageConnector storage;

    public void save(final String name, final Document document)
    {
        final MongoCollection<Document> collection = this.storage.getMainDatabase().getCollection("playerdata_" + name);
        collection.updateOne(new Document("uuid", document.get("uuid")), new Document("$set", document), new UpdateOptions().upsert(true));
    }

    public Document load(final String name, final UUID playerId)
    {
        final MongoCollection<Document> collection = this.storage.getMainDatabase().getCollection("playerdata_" + name);
        return collection.find(new Document("uuid", playerId)).first();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
