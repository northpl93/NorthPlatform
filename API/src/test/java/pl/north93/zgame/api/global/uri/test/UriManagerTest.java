package pl.north93.zgame.api.global.uri.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pl.north93.zgame.api.global.uri.IUriManager;
import pl.north93.zgame.api.standalone.TestApiCore;

public class UriManagerTest
{
    private IUriManager manager;

    @Before
    public void setup()
    {
        final TestApiCore api = TestApiCore.ensureEnvironment();
        this.manager = api.getComponent("API.URI");
    }

    @Test
    public void simpleRoutes()
    {
        this.manager.register("/test1", context -> "test1");

        assertTrue(this.manager.call("test1").equals("test1"));
        assertTrue(this.manager.call("/test1").equals("test1"));
        assertTrue(this.manager.call("/test1/").equals("test1"));
    }

    @Test
    public void paramsInUrl()
    {
        this.manager.register("/test2/:testParam", context -> context.asString("testParam"));
        this.manager.register("/test3/:testParam1/:testParam2", context -> context.asString("testParam1") + context.asString("testParam2"));

        assertTrue(this.manager.call("/test2/testingParams").equals("testingParams"));
        assertTrue(this.manager.call("/test3/1/2").equals("12"));
    }

    @Test
    public void uri() throws URISyntaxException
    {
        this.manager.register("/test4", context -> "test4");
        this.manager.register("/test4/:param", context -> context.asString("param"));

        assertEquals("test4", this.manager.call(new URI("northplatform://test4")));
        assertEquals("test", this.manager.call(new URI("northplatform://test4/test")));
    }
}
