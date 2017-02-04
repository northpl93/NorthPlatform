package pl.north93.zgame.itemshop.shared;

import static java.util.stream.StreamSupport.stream;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mongodb.client.MongoCollection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;

public class ReceiveStorage
{
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector storage;

    public void addReceiveContentFor(final UUID player, final DataEntry dataEntry)
    {
        final MongoCollection<Document> itemshop = this.storage.getMainDatabase().getCollection("itemshop");
        final Document document = this.dataEntryToDocument(dataEntry);
        document.put("uuid", player);
        itemshop.insertOne(document);
    }

    public List<DataEntry> getReceiveContentFor(final UUID player)
    {
        final MongoCollection<Document> itemshop = this.storage.getMainDatabase().getCollection("itemshop");
        return stream(itemshop.find(new Document("uuid", player)).spliterator(), false)
                                                .map(this::docToDataEntry)
                                                .collect(Collectors.toList());
    }

    public void removeReceiveContent(final UUID player, final DataEntry dataEntry)
    {
        final MongoCollection<Document> itemshop = this.storage.getMainDatabase().getCollection("itemshop");
        final Document document = this.dataEntryToDocument(dataEntry);
        document.put("uuid", player);
        itemshop.deleteOne(document);
    }

    private Document dataEntryToDocument(final DataEntry dataEntry)
    {
        final Document doc = new Document();

        doc.put("type", dataEntry.getDataType().toString());
        doc.put("properties", dataEntry.getProperties());

        return doc;
    }

    private DataEntry docToDataEntry(final Document doc)
    {
        //noinspection unchecked
        return new DataEntry(DataType.valueOf(doc.getString("type")), doc.get("properties", Map.class));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
