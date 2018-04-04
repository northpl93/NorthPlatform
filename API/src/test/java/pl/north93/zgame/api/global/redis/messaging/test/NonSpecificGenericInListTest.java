package pl.north93.zgame.api.global.redis.messaging.test;

import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateFactoryImpl;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;
import pl.north93.zgame.api.test.NorthPlatformJunitRunner;

@RunWith(NorthPlatformJunitRunner.class)
public class NonSpecificGenericInListTest
{
    final TemplateManager templateManager = new TemplateManagerImpl(new TemplateFactoryImpl());

    public interface TestInterface
    {
        String getText();
    }

    public static class TestObjectA implements TestInterface
    {
        @Override
        public String getText()
        {
            return "A";
        }
    }

    public static class TestObjectB implements TestInterface
    {
        @Override
        public String getText()
        {
            return "B";
        }
    }

    public static class ObjectWithNonSpecificGenericInList
    {
        public List<TestInterface> testInterfaces;
    }

    @Test
    public void objectWithNonSpecificGenericListTest()
    {
        final ObjectWithNonSpecificGenericInList testObject = new ObjectWithNonSpecificGenericInList();
        testObject.testInterfaces = new ArrayList<>();
        testObject.testInterfaces.add(new TestObjectA());
        testObject.testInterfaces.add(new TestObjectB());

        final byte[] serialised = this.templateManager.serialize(testObject);
        final ObjectWithNonSpecificGenericInList after = this.templateManager.deserialize(ObjectWithNonSpecificGenericInList.class, serialised);

        assertEquals("A", after.testInterfaces.get(0).getText());
        assertEquals("B", after.testInterfaces.get(1).getText());
    }
}
