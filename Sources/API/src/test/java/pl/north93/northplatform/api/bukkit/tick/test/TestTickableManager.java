package pl.north93.northplatform.api.bukkit.tick.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


import java.lang.reflect.Method;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.north93.northplatform.api.bukkit.tick.ITickable;
import pl.north93.northplatform.api.bukkit.tick.Tick;
import pl.north93.northplatform.api.bukkit.tick.impl.TickableManagerImpl;

public class TestTickableManager
{
    private TickableManagerImpl tickableManager;
    private boolean called;
    
    @BeforeEach
    public void setupTickableManager()
    {
        tickableManager = new TickableManagerImpl();
        called = false;
    }
    
    @AfterEach
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
            
            fail();
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
                fail("This never should be called in this case");
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
                    fail("Method is called multiple time in the same tick");
                }
                else
                {
                    called = true;
                }
            }
        };
        
        tickableManager.addTickableObject(tickable);
        callTick();
        
        assertTrue(called);
        called = false;
        
        callTick();
        assertTrue(called);
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
                    fail("Method is called multiple time in the same tick");
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
