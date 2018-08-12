package pl.north93.zgame.api.global.serializer;

import org.junit.Assert;
import org.junit.Test;

import pl.north93.zgame.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.zgame.api.global.serializer.platform.NorthSerializer;
import pl.north93.zgame.api.global.serializer.platform.impl.NorthSerializerImpl;

// serializacja prostych wartosci jako Object - DynamicTemplate
public class DynamicSimpleTypesTest
{
    private final NorthSerializer<byte[]> serializer = new NorthSerializerImpl<>(new MsgPackSerializationFormat());

    @Test
    public void dynamicStringTest()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, "test");
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(String.class, deserialized.getClass());
        Assert.assertEquals("test", deserialized);
    }

    @Test
    public void dynamicBooleanTest()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, true);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(Boolean.class, deserialized.getClass());
        Assert.assertEquals(true, deserialized);
    }

    @Test
    public void dynamicShortTest()
    {
        final short test = 42;

        final byte[] bytes = this.serializer.serialize(Object.class, test);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(Short.class, deserialized.getClass());
        Assert.assertEquals(test, deserialized);
    }

    @Test
    public void dynamicIntegerTest()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, 42);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(Integer.class, deserialized.getClass());
        Assert.assertEquals(42, deserialized);
    }

    @Test
    public void dynamicFloatTest()
    {
        final float PI = 3.14F;

        final byte[] bytes = this.serializer.serialize(Object.class, PI);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(Float.class, deserialized.getClass());
        Assert.assertEquals(PI, deserialized);
    }

    @Test
    public void dynamicDoubleTest()
    {
        final double PI = 3.14D;

        final byte[] bytes = this.serializer.serialize(Object.class, PI);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(Double.class, deserialized.getClass());
        Assert.assertEquals(PI, deserialized);
    }

    @Test
    public void dynamicLongTest()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, 42L);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(Long.class, deserialized.getClass());
        Assert.assertEquals(42L, deserialized);
    }
}
