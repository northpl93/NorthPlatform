package pl.north93.zgame.api.global.serializer;

import org.junit.Assert;
import org.junit.Test;

import pl.north93.zgame.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.zgame.api.global.serializer.platform.NorthSerializer;
import pl.north93.zgame.api.global.serializer.platform.impl.NorthSerializerImpl;

// serializacja prostych wartosci
public class SimpleTypesTest
{
    private final NorthSerializer<byte[]> serializer = new NorthSerializerImpl<>(new MsgPackSerializationFormat());

    @Test
    public void stringTest()
    {
        final byte[] bytes = this.serializer.serialize(String.class, "test");
        final Object deserialized = this.serializer.deserialize(String.class, bytes);

        Assert.assertSame(String.class, deserialized.getClass());
        Assert.assertEquals("test", deserialized);
    }

    @Test
    public void booleanTest()
    {
        final byte[] bytes = this.serializer.serialize(Boolean.class, true);
        final Object deserialized = this.serializer.deserialize(Boolean.class, bytes);

        Assert.assertSame(Boolean.class, deserialized.getClass());
        Assert.assertEquals(true, deserialized);
    }

    @Test
    public void shortTest()
    {
        final short test = 42;

        final byte[] bytes = this.serializer.serialize(Short.class, test);
        final Object deserialized = this.serializer.deserialize(Short.class, bytes);

        Assert.assertSame(Short.class, deserialized.getClass());
        Assert.assertEquals(test, deserialized);
    }

    @Test
    public void integerTest()
    {
        final byte[] bytes = this.serializer.serialize(Integer.class, 42);
        final Object deserialized = this.serializer.deserialize(Integer.class, bytes);

        Assert.assertSame(Integer.class, deserialized.getClass());
        Assert.assertEquals(42, deserialized);
    }

    @Test
    public void floatTest()
    {
        final float PI = 3.14F;

        final byte[] bytes = this.serializer.serialize(Float.class, PI);
        final Object deserialized = this.serializer.deserialize(Float.class, bytes);

        Assert.assertSame(Float.class, deserialized.getClass());
        Assert.assertEquals(PI, deserialized);
    }

    @Test
    public void doubleTest()
    {
        final double PI = 3.14D;

        final byte[] bytes = this.serializer.serialize(Double.class, PI);
        final Object deserialized = this.serializer.deserialize(Double.class, bytes);

        Assert.assertSame(Double.class, deserialized.getClass());
        Assert.assertEquals(PI, deserialized);
    }

    @Test
    public void longTest()
    {
        final byte[] bytes = this.serializer.serialize(Long.class, 42L);
        final Object deserialized = this.serializer.deserialize(Long.class, bytes);

        Assert.assertSame(Long.class, deserialized.getClass());
        Assert.assertEquals(42L, deserialized);
    }
}
