package pl.north93.zgame.api.bukkit.tick.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import org.spigotmc.SneakyThrow;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class TickableManagerImpl extends Component implements ITickableManager
{
    private final Map<Class<? extends ITickable>, Method[]> registeredTickableClasses = new WeakHashMap<>();
    private final Set<Collection<? extends ITickable>> tickableObjectsCollection = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<TickableWeakReference> tickableObjects = new HashSet<>();

    @Inject
    private BukkitApiCore apiCore;
    
    private BukkitTask tickTask;
    
    @Override
    protected void enableComponent()
    {
        tickTask = Bukkit.getScheduler().runTaskTimer(apiCore.getPluginMain(), () -> onTick(), 1, 1);
    }

    @Override
    protected void disableComponent()
    {
        if ( tickTask != null )
        {
            tickTask.cancel();
        }
    }
    
    private Method[] findTickableMethodsInClass(Class<?> clazz)
    {
        try
        {
            if ( !ITickable.class.isAssignableFrom(clazz) )
            {
                return null;
            }
            
            List<Method> tickableMethods = new ArrayList<>();
            
            for ( Method method : clazz.getDeclaredMethods() )
            {
                int modifiers = method.getModifiers();
                if ( Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers) || method.isSynthetic() )
                {
                    continue;
                }
                
                Tick annotation = method.getAnnotation(Tick.class);
                if ( annotation == null )
                {
                    continue;
                }
                
                if ( method.getReturnType() != void.class && method.getParameterTypes().length != 0 )
                {
                    apiCore.getLogger().warning("TickHandler method " + method.getName() + " in class " + clazz.getName()
                            + " has invalid signature" + " (shold be 'void " + method.getName() + "()' )");
                    continue;
                }
                
                method.setAccessible(true);
                tickableMethods.add(method);
            }
            
            return tickableMethods.toArray(new Method[tickableMethods.size()]);
        }
        catch ( Throwable e )
        {
            SneakyThrow.sneaky(e);
            return null;
        }
    }
    
    @Override
    public void addTickableObject(ITickable tickable)
    {
        Preconditions.checkArgument(tickable != null);
        tickableObjects.add(new TickableWeakReference(tickable));
    }
    
    @Override
    public void removeTickableObject(ITickable tickableToRemove)
    {
        Preconditions.checkArgument(tickableToRemove != null);
        tickableObjects.remove(new TickableWeakReference(tickableToRemove));
    } 
    
    @Override
    public void addTickableObjectsCollection(Collection<? extends ITickable> objects)
    {
        tickableObjectsCollection.add(objects);
    }

    @Override
    public void removeTickableObjectsCollection(Collection<? extends ITickable> objects)
    {
        tickableObjectsCollection.remove(objects);
    }
    
    private void onTick()
    {
        for ( Collection<? extends ITickable> objects : tickableObjectsCollection )
        {
            for ( ITickable tickable : objects )
            {
                handle(tickable);
            }
        }
        
        Iterator<TickableWeakReference> it = tickableObjects.iterator();
        
        while ( it.hasNext() )
        {
            ITickable tickable = it.next().get();
            if ( tickable == null )
            {
                // tickable has been garbage collected
                it.remove();
            }
        }
        
        // second loop is need for prevent concurrent modification exception
        for ( TickableWeakReference tickableRef : new HashSet<>(tickableObjects) )
        {
            ITickable tickable = tickableRef.get();
            if ( tickable != null )
            {
                handle(tickable);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handle(ITickable tickable)
    {
        Class<?> cls = tickable.getClass();
        while ( cls != null )
        {
            Method[] methods = registeredTickableClasses.computeIfAbsent((Class<? extends ITickable>) cls, this::findTickableMethodsInClass);
            
            if ( methods == null ) // Obecna klasa nie dziedziczy po ITickable, więc pozostałe podklasy tez nie
            {
                return;
            }
            
            for ( Method method : methods )
            {
                try
                {
                    method.invoke(tickable);
                }
                catch ( InvocationTargetException e )
                {
                    reportTickHandlerException(tickable, e);
                }
                catch ( Throwable e )
                {
                    SneakyThrow.sneaky(e);
                }
            }
            
            cls = cls.getSuperclass();
        }
    }

    private void reportTickHandlerException(ITickable tickable, InvocationTargetException e)
    {
        Throwable cause = e.getCause();
        apiCore.getLogger().severe("An exception was thrown when ticking (" + tickable.toString() + "):");
        cause.printStackTrace();
    }
}
