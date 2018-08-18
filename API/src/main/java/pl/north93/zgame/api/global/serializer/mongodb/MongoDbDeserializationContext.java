package pl.north93.zgame.api.global.serializer.mongodb;

import java.util.Collection;

import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.codecs.BsonDocumentCodec;

import lombok.ToString;
import pl.north93.zgame.api.global.serializer.mongodb.reader.NorthBsonReader;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

@ToString
public class MongoDbDeserializationContext extends DeserializationContext
{
    private static final BsonDocumentCodec CODEC = new BsonDocumentCodec();
    private final NorthBsonReader reader;

    public MongoDbDeserializationContext(final TemplateEngine templateEngine, final BsonReader reader)
    {
        super(templateEngine);

        final BsonDocument bsonDocument = CODEC.decode(reader, null);
        this.reader = new NorthBsonReader(bsonDocument);
    }

    public void readStartArray(final FieldInfo field)
    {
        this.reader.enterArray(field.getName());
    }

    public void readEndArray(final FieldInfo field)
    {
        this.reader.exitArray(field.getName());
    }

    public boolean hasMore() // uzywane glównie w tablicach/listach
    {
        return this.reader.hasMore();
    }

    public Collection<String> getKeys() // uzywane glównie w mapach
    {
        return this.reader.getKeys();
    }

    @Override
    public void enterObject(final FieldInfo field)
    {
        this.reader.enterObject(field.getName());
    }

    @Override
    public void exitObject(final FieldInfo field)
    {
        this.reader.exitObject(field.getName());
    }

    @Override
    public boolean trySkipNull(final FieldInfo field) throws Exception
    {
        return ! this.reader.containsKey(field.getName());
    }

    @Override
    public String readString(final FieldInfo field) throws Exception
    {
        return this.reader.readString(field.getName());
    }

    @Override
    public Boolean readBoolean(final FieldInfo field) throws Exception
    {
        return this.reader.readBoolean(field.getName());
    }

    @Override
    public Byte readByte(final FieldInfo field) throws Exception
    {
        return (byte) this.reader.readInt32(field.getName());
    }

    @Override
    public Short readShort(final FieldInfo field) throws Exception
    {
        return (short) this.reader.readInt32(field.getName());
    }

    @Override
    public Integer readInteger(final FieldInfo field) throws Exception
    {
        return this.reader.readInt32(field.getName());
    }

    @Override
    public Float readFloat(final FieldInfo field) throws Exception
    {
        return (float) this.reader.readDouble(field.getName());
    }

    @Override
    public Double readDouble(final FieldInfo field) throws Exception
    {
        return this.reader.readDouble(field.getName());
    }

    @Override
    public Long readLong(final FieldInfo field) throws Exception
    {
        return this.reader.readInt64(field.getName());
    }

    public BsonBinary readBinary(final FieldInfo field)
    {
        return this.reader.readBinary(field.getName());
    }

    public BsonType readType(final FieldInfo field)
    {
        return this.reader.readType(field.getName());
    }
}
