package pl.north93.zgame.api.global.serializer.mongodb.test;

import java.io.StringWriter;

import org.bson.BsonReader;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.junit.Assert;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.zgame.api.global.serializer.mongodb.MongoDbCodec;
import pl.north93.zgame.api.global.serializer.mongodb.MongoDbSerializationFormat;
import pl.north93.zgame.api.global.serializer.platform.NorthSerializer;
import pl.north93.zgame.api.global.serializer.platform.impl.NorthSerializerImpl;

public class MongoDbSimpleObjectsTest
{
    private final NorthSerializer<BsonReader> serializer = new NorthSerializerImpl<>(new MongoDbSerializationFormat());

    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleObject
    {
        String string;
        Integer integer;
        Double aDouble;
    }

    @Test
    public void testSimpleObjectSerialization()
    {
        final SimpleObject beforeSerialization = new SimpleObject("testString", 10, 5.0D);

        final StringWriter stringWriter = new StringWriter();
        MongoDbCodec.writer.set(new JsonWriter(stringWriter));
        this.serializer.serialize(SimpleObject.class, beforeSerialization);
        System.out.println(stringWriter);
        final Object deserialized = this.serializer.deserialize(SimpleObject.class, new JsonReader(stringWriter.toString()));

        Assert.assertSame(SimpleObject.class, deserialized.getClass());
        Assert.assertEquals(beforeSerialization, deserialized);
    }

    @Test
    public void testSimpleObjectDynamicSerialization()
    {
        final SimpleObject beforeSerialization = new SimpleObject("testString", 10, 5.0D);

        final StringWriter stringWriter = new StringWriter();
        MongoDbCodec.writer.set(new JsonWriter(stringWriter));
        this.serializer.serialize(Object.class, beforeSerialization);
        System.out.println(stringWriter);
        final Object deserialized = this.serializer.deserialize(Object.class, new JsonReader(stringWriter.toString()));

        Assert.assertSame(SimpleObject.class, deserialized.getClass());
        Assert.assertEquals(beforeSerialization, deserialized);
    }
}
