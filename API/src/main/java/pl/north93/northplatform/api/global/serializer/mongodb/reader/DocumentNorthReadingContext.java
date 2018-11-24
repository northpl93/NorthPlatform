package pl.north93.northplatform.api.global.serializer.mongodb.reader;

import java.util.Collection;

import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonRegularExpression;
import org.bson.BsonType;
import org.bson.types.ObjectId;

/*default*/ class DocumentNorthReadingContext extends NorthReadingContext
{
    private final BsonDocument document;

    public DocumentNorthReadingContext(final BsonDocument document)
    {
        this.document = document;
    }

    @Override
    public NorthReadingContext enterObject(final String name)
    {
        final BsonDocument document = this.document.getDocument(name);
        return new DocumentNorthReadingContext(document);
    }

    @Override
    public NorthReadingContext enterArray(final String name)
    {
        final BsonArray array = this.document.getArray(name);
        return new ArrayNorthReadingContext(array);
    }

    @Override
    public boolean hasMore()
    {
        return true;
    }

    @Override
    public boolean containsKey(final String name)
    {
        return this.document.containsKey(name);
    }

    @Override
    public Collection<String> getKeys()
    {
        return this.document.keySet();
    }

    @Override
    public BsonType readType(final String name)
    {
        return this.document.get(name).getBsonType();
    }

    @Override
    public String readString(final String name)
    {
        return this.document.getString(name).getValue();
    }

    @Override
    public boolean readBoolean(final String name)
    {
        return this.document.getBoolean(name).getValue();
    }

    @Override
    public int readInt32(final String name)
    {
        return this.document.getInt32(name).getValue();
    }

    @Override
    public double readDouble(final String name)
    {
        return this.document.getDouble(name).getValue();
    }

    @Override
    public long readInt64(final String name)
    {
        return this.document.getInt64(name).getValue();
    }

    @Override
    public ObjectId readObjectId(final String name)
    {
        return this.document.getObjectId(name).getValue();
    }

    @Override
    public BsonBinary readBinary(final String name)
    {
        return this.document.getBinary(name);
    }

    @Override
    public BsonRegularExpression readRegularExpression(final String name)
    {
        return this.document.getRegularExpression(name);
    }
}
