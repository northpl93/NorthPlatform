package pl.north93.zgame.api.global.redis.messaging.test;

import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateFactoryImpl;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;

public class CustomTemplateWithGenericTest
{
    final TemplateManager templateManager = new TemplateManagerImpl(new TemplateFactoryImpl());

    public static final class TestObjectWithGeneric
    {
        @MsgPackCustomTemplate(ArrayListTemplate.class)
        private List<String> testList1;
        @MsgPackCustomTemplate(ArrayListTemplate.class)
        private List<Integer> testList2;
    }

    @Test
    public void customTemplateWithGenericType()
    {
        final TestObjectWithGeneric before = new TestObjectWithGeneric();
        before.testList1 = new ArrayList<>();
        before.testList1.add("testList1");
        before.testList2 = new ArrayList<>();
        before.testList2.add(42);

        final byte[] serialized = this.templateManager.serialize(TestObjectWithGeneric.class, before);
        final TestObjectWithGeneric after = this.templateManager.deserialize(TestObjectWithGeneric.class, serialized);

        assertEquals(1, after.testList1.size());
        assertEquals(1, after.testList2.size());

        assertEquals("testList1", after.testList1.get(0));
        assertEquals(Integer.valueOf(42), after.testList2.get(0));
    }
}
