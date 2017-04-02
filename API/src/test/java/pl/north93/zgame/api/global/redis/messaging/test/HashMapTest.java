package pl.north93.zgame.api.global.redis.messaging.test;

import static org.junit.Assert.assertEquals;


import java.util.HashMap;

import org.junit.Test;

import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateFactoryImpl;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;

public class HashMapTest
{
    final TemplateManager templateManager = new TemplateManagerImpl(new TemplateFactoryImpl());

    public static final class ConcreteHashMap
    {
        private HashMap<Integer, String> hashMap;
    }

    @Test
    public void concreteHashMapTest()
    {
        final ConcreteHashMap before = new ConcreteHashMap();
        before.hashMap = new HashMap<>();
        before.hashMap.put(1, "test1");
        before.hashMap.put(2, "test2");

        final byte[] serialized = this.templateManager.serialize(ConcreteHashMap.class, before);
        final ConcreteHashMap after = this.templateManager.deserialize(ConcreteHashMap.class, serialized);

        assertEquals("test1", after.hashMap.get(1));
        assertEquals("test2", after.hashMap.get(2));
    }
}
