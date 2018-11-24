package pl.north93.northplatform.api.global.serializer.msgpack.test;

import org.junit.Assert;
import org.junit.Test;

import pl.north93.northplatform.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.northplatform.api.global.serializer.platform.NorthSerializer;
import pl.north93.northplatform.api.global.serializer.platform.impl.NorthSerializerImpl;

public class MsgPackEnumsTest
{
    private final NorthSerializer<byte[]> serializer = new NorthSerializerImpl<>(new MsgPackSerializationFormat());

    public enum TestEnum
    {
        TEST_1, TEST_2, TEST_3, TEST_4, TEST_5
    }

    public enum TestExtendedEnum
    {
        TEST_1
                {
                    @Override
                    void test()
                    {
                    }
                };

        abstract void test();
    }

    @Test
    public void serializeSimpleEnumValue()
    {
        final byte[] bytes = this.serializer.serialize(TestEnum.class, TestEnum.TEST_2);
        final Object deserialized = this.serializer.deserialize(TestEnum.class, bytes);

        Assert.assertSame(TestEnum.class, deserialized.getClass());
        Assert.assertEquals(TestEnum.TEST_2, deserialized);
    }

    @Test
    public void serializeDynamicEnumValue()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, TestEnum.TEST_2);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertSame(TestEnum.class, deserialized.getClass());
        Assert.assertEquals(TestEnum.TEST_2, deserialized);
    }

    @Test
    public void serializeSimpleExtendedEnumValue()
    {
        final byte[] bytes = this.serializer.serialize(TestExtendedEnum.class, TestExtendedEnum.TEST_1);
        final Object deserialized = this.serializer.deserialize(TestExtendedEnum.class, bytes);

        Assert.assertEquals(TestExtendedEnum.TEST_1, deserialized);
    }

    @Test
    public void serializeDynamicExtendedEnumValue()
    {
        final byte[] bytes = this.serializer.serialize(Object.class, TestExtendedEnum.TEST_1);
        final Object deserialized = this.serializer.deserialize(Object.class, bytes);

        Assert.assertEquals(TestExtendedEnum.TEST_1, deserialized);
    }
}
