package pl.north93.zgame.api.global.redis.messaging.test;

import static org.junit.Assert.assertEquals;


import static pl.north93.zgame.api.global.redis.messaging.test.ArrayTest.TestEnum.TEST1;
import static pl.north93.zgame.api.global.redis.messaging.test.ArrayTest.TestEnum.TEST2;


import org.junit.Test;

import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateFactoryImpl;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;

public class ArrayTest
{
    final TemplateManager templateManager = new TemplateManagerImpl(new TemplateFactoryImpl());

    public enum TestEnum
    {
        TEST1, TEST2
    }

    public static final class SpecificArrayType
    {
        private String[] strings;
    }

    public static final class UnSpecificArrayType
    {
        private Object[] objects;
    }

    @Test
    public void concreteArrayTest()
    {
        final SpecificArrayType before = new SpecificArrayType();
        before.strings = new String[] {"test1", "test2"};

        final byte[] serialized = this.templateManager.serialize(SpecificArrayType.class, before);
        final SpecificArrayType after = this.templateManager.deserialize(SpecificArrayType.class, serialized);

        assertEquals("test1", after.strings[0]);
        assertEquals("test2", after.strings[1]);
    }

    @Test
    public void unConcreteArrayTest()
    {
        final UnSpecificArrayType before = new UnSpecificArrayType();
        before.objects = new Object[] {5, "testString", 10L};

        final byte[] serialized = this.templateManager.serialize(UnSpecificArrayType.class, before);
        final UnSpecificArrayType after = this.templateManager.deserialize(UnSpecificArrayType.class, serialized);

        assertEquals(5, after.objects[0]);
        assertEquals("testString", after.objects[1]);
        assertEquals(10L, after.objects[2]);
    }

    @Test
    public void unConcreteArrayTestWithEnum() // regression test. Enum in dynamic template causing hang
    {
        final UnSpecificArrayType before = new UnSpecificArrayType();
        before.objects = new Object[] {TEST1, TEST2, TEST1};

        final byte[] serialized = this.templateManager.serialize(UnSpecificArrayType.class, before);
        final UnSpecificArrayType after = this.templateManager.deserialize(UnSpecificArrayType.class, serialized);

        assertEquals(TEST1, after.objects[0]);
        assertEquals(TEST2, after.objects[1]);
        assertEquals(TEST1, after.objects[2]);
    }
}
