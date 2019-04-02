package pl.north93.northplatform.api.global.uri.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.uri.IUriManager;
import pl.north93.northplatform.api.test.NorthPlatformJunitExtension;

@ExtendWith(NorthPlatformJunitExtension.class)
public class UriManagerTest
{
    @Inject
    private IUriManager manager;

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
