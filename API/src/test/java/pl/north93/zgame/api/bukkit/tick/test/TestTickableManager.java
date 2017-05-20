package pl.north93.zgame.api.bukkit.tick.test;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.bukkit.tick.impl.TickableManagerImpl;

public class TestTickableManager
{
    private TickableManagerImpl tickableManager;
    private boolean called;
    
    @Before
    public void setupTickableManager()
    {
        tickableManager = new TickableManagerImpl();
        called = false;
    }
    
    @After
    public void cleanupTickableManger()
    {
        tickableManager = null;
    }
    
    private void callTick()
    {
        try {
        
            Method tick = TickableManagerImpl.class.getDeclaredMethod("onTick");
            tick.setAccessible(true);
            tick.invoke(tickableManager);
        } catch ( Throwable e ) {
            
            Assert.fail();
        }
    }
    
    @Test
    public void testTickableObjectAddRemove()
    {
        ITickable tickable = new ITickable()
        {
            @Tick
            public void thisNeverShouldBeCalled()
            {
                Assert.fail("This never should be called in this case");
            }
        };
        
        tickableManager.addTickableObject(tickable);
        tickableManager.removeTickableObject(tickable);
        callTick();
    }
    
    @Test
    public void testTickableTick()
    {
        ITickable tickable = new ITickable()
        {
            @Tick
            public void tick()
            {
                if ( called )
                {
                    Assert.fail("Method is called multiple time in the same tick");
                }
                else
                {
                    called = true;
                }
            }
        };
        
        tickableManager.addTickableObject(tickable);
        callTick();
        
        Assert.assertTrue(called);
        called = false;
        
        callTick();
        Assert.assertTrue(called);
    }
    
    @Test
    public void testTickableMultipleAddTheSameObject()
    {
        ITickable tickable = new ITickable()
        {
            @Tick
            public void tick()
            {
                if ( called )
                {
                    Assert.fail("Method is called multiple time in the same tick");
                }
                else
                {
                    called = true;
                }
            }
        };
        
        tickableManager.addTickableObject(tickable);
        tickableManager.addTickableObject(tickable);
        callTick();
    }
}
