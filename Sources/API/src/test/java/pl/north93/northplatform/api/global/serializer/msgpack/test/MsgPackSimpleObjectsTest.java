package pl.north93.northplatform.api.global.serializer.msgpack.test;

import org.junit.Assert;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.northplatform.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.northplatform.api.global.serializer.platform.NorthSerializer;
import pl.north93.northplatform.api.global.serializer.platform.impl.NorthSerializerImpl;

public class MsgPackSimpleObjectsTest
{
    private final NorthSerializer<byte[]> serializer = new NorthSerializerImpl<>(new MsgPackSerializationFormat());

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

        final byte[] bytes = this.serializer.serialize(SimpleObject.class, beforeSerialization);
        final Object deserialized = this.serializer.deserialize(SimpleObject.class, bytes);

        Assert.assertSame(SimpleObject.class, deserialized.getClass());
        Assert.assertEquals(beforeSerialization, deserialized);
    }

    @Test
    public void testSimpleObjectDynamicSerialization()
    {
        final SimpleObject beforeSerialization = new SimpleObject("testString", 10, 5.0D);

        final byte[] bytes = this.serializer.serialize(Object.class, beforeSerialization);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(SimpleObject.class, deserialized.getClass());
        Assert.assertEquals(beforeSerialization, deserialized);
    }
}
