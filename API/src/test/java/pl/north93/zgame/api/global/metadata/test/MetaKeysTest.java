package pl.north93.zgame.api.global.metadata.test;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

import pl.north93.zgame.api.global.metadata.MetaKey;

public class MetaKeysTest
{
    @Test
    public void sameInstancesTest()
    {
        final MetaKey test1 = MetaKey.get("keytest1");
        final MetaKey test2 = MetaKey.get("keytest1");

        assertEquals(true, test1 == test2);
    }

    @Test
    public void caseInsensitiveKeysTest()
    {
        final MetaKey test1 = MetaKey.get("keytest2");
        final MetaKey test2 = MetaKey.get("keYTEst2");

        assertEquals(true, test1 == test2);
    }
}
