package pl.north93.zgame.api.global.redis.messaging.test;

import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackNullable;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateFactoryImpl;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;

public class SimpleTest
{
    final TemplateManager templateManager = new TemplateManagerImpl(new TemplateFactoryImpl());

    public static final class SimpleTestObject
    {
        public SimpleTestObject()
        {
        }

        public SimpleTestObject(final String stringField, final Boolean booleanField, final Integer integerField)
        {
            this.stringField = stringField;
            this.booleanField = booleanField;
            this.integerField = integerField;
        }

        private @MsgPackNullable String  stringField;
        private @MsgPackNullable Boolean booleanField;
        private @MsgPackNullable Integer integerField;
    }

    @Test
    public void simpleObjectTest()
    {
        final SimpleTestObject before = new SimpleTestObject("test1", true, 42);

        final byte[] serialized = this.templateManager.serialize(SimpleTestObject.class, before);
        final SimpleTestObject after = this.templateManager.deserialize(SimpleTestObject.class, serialized);

        assertEquals(before.stringField, after.stringField);
        assertEquals(before.booleanField, after.booleanField);
        assertEquals(before.integerField, after.integerField);
    }

    @Test
    public void simpleNullTest()
    {
        final byte[] serialized = this.templateManager.serialize(SimpleTestObject.class, new SimpleTestObject());
        final SimpleTestObject after = this.templateManager.deserialize(SimpleTestObject.class, serialized);

        assertEquals(null, after.stringField);
        assertEquals(null, after.booleanField);
        assertEquals(null, after.integerField);
    }

    @Test
    public void simpleObjectListTest()
    {
        final ArrayList<SimpleTestObject> listBefore = new ArrayList<>();
        listBefore.add(new SimpleTestObject("test1", true, 42));
        listBefore.add(new SimpleTestObject("test2", false, 10));

        final byte[] serialized = this.templateManager.serializeList(SimpleTestObject.class, listBefore);
        final List<SimpleTestObject> listAfter = this.templateManager.deserializeList(SimpleTestObject.class, serialized);

        assertEquals(2, listAfter.size());

        assertEquals("test1", listAfter.get(0).stringField);
        assertEquals(true, listAfter.get(0).booleanField);
        assertEquals(Integer.valueOf(42), listAfter.get(0).integerField);

        assertEquals("test2", listAfter.get(1).stringField);
        assertEquals(false, listAfter.get(1).booleanField);
        assertEquals(Integer.valueOf(10), listAfter.get(1).integerField);
    }
}
