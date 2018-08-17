package pl.north93.zgame.api.global.serializer.mongodb;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import pl.north93.zgame.api.global.serializer.platform.NorthSerializer;

public class MongoDbCodec<T> implements Codec<T>
{
    public static final ThreadLocal<BsonWriter> writer = new ThreadLocal<>();
    private final NorthSerializer<BsonReader> serializer;
    private final Class<T> type;

    public MongoDbCodec(final NorthSerializer<BsonReader> serializer, final Class<T> type)
    {
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    public T decode(final BsonReader bsonReader, final DecoderContext decoderContext)
    {
        return this.serializer.deserialize(this.type, bsonReader);
    }

    @Override
    public void encode(final BsonWriter bsonWriter, final Object o, final EncoderContext encoderContext)
    {
        try
        {
            writer.set(bsonWriter);
            this.serializer.serialize(this.type, o);
        }
        finally
        {
            writer.remove();
        }
    }

    @Override
    public Class<T> getEncoderClass()
    {
        return this.type;
    }
}
