package pl.north93.zgame.api.global.serializer.msgpack.test;

import org.junit.Assert;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.zgame.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.zgame.api.global.serializer.platform.NorthSerializer;
import pl.north93.zgame.api.global.serializer.platform.impl.NorthSerializerImpl;

public class MsgPackArraysTest
{
    private final NorthSerializer<byte[]> serializer = new NorthSerializerImpl<>(new MsgPackSerializationFormat());

    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ObjectWithArrays
    {
        String[] strings;
        Object[] objects;
    }

    @Test
    public void directArrayOfStringSerialization()
    {
        final String[] beforeSerialization = new String[] {"test1", "test2"};

        final byte[] bytes = this.serializer.serialize(String[].class, beforeSerialization);
        final Object deserialized = this.serializer.deserialize(String[].class, bytes);

        Assert.assertSame(String[].class, deserialized.getClass());
        Assert.assertArrayEquals(beforeSerialization, (Object[]) deserialized);
    }

    @Test
    public void objectWithArraysSerialization()
    {
        final ObjectWithArrays before = new ObjectWithArrays(new String[]{"testString1", "testString2"}, new Object[]{10, false});

        final byte[] bytes = this.serializer.serialize(ObjectWithArrays.class, before);
        final Object deserialized = this.serializer.deserialize(ObjectWithArrays.class, bytes);

        Assert.assertSame(ObjectWithArrays.class, deserialized.getClass());

        final ObjectWithArrays after = (ObjectWithArrays) deserialized;
        Assert.assertArrayEquals(before.strings, after.strings);
        Assert.assertArrayEquals(before.objects, after.objects);
    }
}
