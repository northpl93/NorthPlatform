package pl.north93.northplatform.api.global.serializer.msgpack.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


import org.junit.jupiter.api.Test;

import pl.north93.northplatform.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.northplatform.api.global.serializer.platform.NorthSerializer;
import pl.north93.northplatform.api.global.serializer.platform.impl.NorthSerializerImpl;

// serializacja prostych wartosci jako Object - DynamicTemplate
public class MsgPackDynamicSimpleTypesTest
{
    private final NorthSerializer<byte[]> serializer = new NorthSerializerImpl<>(new MsgPackSerializationFormat());

    @Test
    public void dynamicStringTest()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, "test");
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        assertSame(String.class, deserialized.getClass());
        assertEquals("test", deserialized);
    }

    @Test
    public void dynamicBooleanTest()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, true);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        assertSame(Boolean.class, deserialized.getClass());
        assertEquals(true, deserialized);
    }

    @Test
    public void dynamicShortTest()
    {
        final short test = 42;

        final byte[] bytes = this.serializer.serialize(Object.class, test);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        assertSame(Short.class, deserialized.getClass());
        assertEquals(test, deserialized);
    }

    @Test
    public void dynamicIntegerTest()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, 42);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        assertSame(Integer.class, deserialized.getClass());
        assertEquals(42, deserialized);
    }

    @Test
    public void dynamicFloatTest()
    {
        final float PI = 3.14F;

        final byte[] bytes = this.serializer.serialize(Object.class, PI);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        assertSame(Float.class, deserialized.getClass());
        assertEquals(PI, deserialized);
    }

    @Test
    public void dynamicDoubleTest()
    {
        final double PI = 3.14D;

        final byte[] bytes = this.serializer.serialize(Object.class, PI);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        assertSame(Double.class, deserialized.getClass());
        assertEquals(PI, deserialized);
    }

    @Test
    public void dynamicLongTest()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, 42L);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        assertSame(Long.class, deserialized.getClass());
        assertEquals(42L, deserialized);
    }
}
