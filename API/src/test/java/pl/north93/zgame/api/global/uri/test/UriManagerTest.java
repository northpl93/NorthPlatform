package pl.north93.zgame.api.global.uri.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import pl.north93.zgame.api.global.uri.IUriManager;
import pl.north93.zgame.api.global.uri.impl.UriManagerImpl;

public class UriManagerTest
{
    private IUriManager manager = new UriManagerImpl();

    @Test
    public void simpleRoutes()
    {
        this.manager.register("/test1", (uri, params) -> "test1");

        assertTrue(this.manager.call("test1").equals("test1"));
        assertTrue(this.manager.call("/test1").equals("test1"));
        assertTrue(this.manager.call("/test1/").equals("test1"));
    }

    @Test
    public void paramsInUrl()
    {
        this.manager.register("/test2/:testParam", (uri, params) -> params.get("testParam"));
        this.manager.register("/test3/:testParam1/:testParam2", (uri, params) -> params.get("testParam1") + params.get("testParam2"));

        assertTrue(this.manager.call("/test2/testingParams").equals("testingParams"));
        assertTrue(this.manager.call("/test3/1/2").equals("12"));
    }

    @Test
    public void uri() throws URISyntaxException
    {
        this.manager.register("/test4", (uri, params) -> "test4");
        this.manager.register("/test4/:param", (uri, params) -> params.get("param"));

        assertEquals("test4", this.manager.call(new URI("northplatform://test4")));
        assertEquals("test", this.manager.call(new URI("northplatform://test4/test")));
    }
}
