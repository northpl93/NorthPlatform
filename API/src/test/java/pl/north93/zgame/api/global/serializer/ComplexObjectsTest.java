package pl.north93.zgame.api.global.serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.zgame.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.zgame.api.global.serializer.platform.NorthSerializer;
import pl.north93.zgame.api.global.serializer.platform.impl.NorthSerializerImpl;

public class ComplexObjectsTest
{
    private final NorthSerializer<byte[]> serializer = new NorthSerializerImpl<>(new MsgPackSerializationFormat());

    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplexObject
    {
        Object object;
        List<Object> objects;
    }

    @Test
    public void testComplexObjectSerialization()
    {
        final ComplexObject beforeSerialization = new ComplexObject("test", new ArrayList<>(Arrays.asList(5, false)));

        final byte[] bytes = this.serializer.serialize(ComplexObject.class, beforeSerialization);
        final Object deserialized = this.serializer.deserialize(ComplexObject.class, bytes);

        Assert.assertSame(ComplexObject.class, deserialized.getClass());
        Assert.assertEquals(beforeSerialization, deserialized);
    }
}
