package pl.north93.northplatform.api.global.serializer.mongodb.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bson.BsonReader;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.northplatform.api.global.serializer.mongodb.MongoDbCodec;
import pl.north93.northplatform.api.global.serializer.mongodb.MongoDbSerializationFormat;
import pl.north93.northplatform.api.global.serializer.platform.NorthSerializer;
import pl.north93.northplatform.api.global.serializer.platform.impl.NorthSerializerImpl;

public class MongoDbListsTest
{
    private final NorthSerializer<BsonReader> serializer = new NorthSerializerImpl<>(new MongoDbSerializationFormat());

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArrayListHolder
    {
        ArrayList list;
    }

    @Test
    public void emptyArrayListTest()
    {
        final StringWriter stringWriter = new StringWriter();
        MongoDbCodec.writer.set(new JsonWriter(stringWriter));
        this.serializer.serialize(ArrayListHolder.class, new ArrayListHolder(new ArrayList<>()));
        System.out.println(stringWriter);
        final Object deserialized = this.serializer.deserialize(ArrayListHolder.class, new JsonReader(stringWriter.toString()));

        assertSame(ArrayListHolder.class, deserialized.getClass());

        final ArrayListHolder deserializedHolder = (ArrayListHolder) deserialized;
        assertEquals(0, deserializedHolder.list.size());
    }

    @Test
    public void arrayListWithStrings()
    {
        final ArrayList<String> strings = new ArrayList<>(Arrays.asList("test1", "test2", "test3"));

        final StringWriter stringWriter = new StringWriter();
        MongoDbCodec.writer.set(new JsonWriter(stringWriter));
        this.serializer.serialize(ArrayListHolder.class, new ArrayListHolder(strings));
        System.out.println(stringWriter);
        final Object deserialized = this.serializer.deserialize(ArrayListHolder.class, new JsonReader(stringWriter.toString()));

        assertSame(ArrayListHolder.class, deserialized.getClass());

        final ArrayListHolder deserializedHolder = (ArrayListHolder) deserialized;
        assertEquals(strings, deserializedHolder.list);
    }

    @Test
    public void arrayListWithManyTypes()
    {
        final ArrayList<Object> strings = new ArrayList<>(Arrays.asList(100, "test", true));

        final StringWriter stringWriter = new StringWriter();
        MongoDbCodec.writer.set(new JsonWriter(stringWriter));
        this.serializer.serialize(ArrayListHolder.class, new ArrayListHolder(strings));
        System.out.println(stringWriter);
        final Object deserialized = this.serializer.deserialize(ArrayListHolder.class, new JsonReader(stringWriter.toString()));

        assertSame(ArrayListHolder.class, deserialized.getClass());

        final ArrayListHolder deserializedHolder = (ArrayListHolder) deserialized;
        assertEquals(strings, deserializedHolder.list);
    }

    @Test
    public void nestedArrayLists()
    {
        final ArrayList<String> nestedList = new ArrayList<>(Collections.singletonList("test"));
        final ArrayList<ArrayList<String>> outerList = new ArrayList<>(Collections.singletonList(nestedList));

        final StringWriter stringWriter = new StringWriter();
        MongoDbCodec.writer.set(new JsonWriter(stringWriter));
        this.serializer.serialize(ArrayListHolder.class, new ArrayListHolder(outerList));
        System.out.println(stringWriter);
        final Object deserialized = this.serializer.deserialize(ArrayListHolder.class, new JsonReader(stringWriter.toString()));

        assertSame(ArrayListHolder.class, deserialized.getClass());

        final ArrayListHolder deserializedHolder = (ArrayListHolder) deserialized;
        assertEquals(outerList, deserializedHolder.list);
    }
}
