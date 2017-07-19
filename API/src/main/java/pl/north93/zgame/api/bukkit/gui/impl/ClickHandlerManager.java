package pl.north93.zgame.api.bukkit.gui.impl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import org.spigotmc.SneakyThrow;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pl.north93.zgame.api.bukkit.gui.ClickEvent;
import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.bukkit.gui.Gui;

public class ClickHandlerManager
{
    private final Map<Class<?>, Multimap<String, Method>> resolvedGuiClasses = new WeakHashMap<>();
    
    private Multimap<String, Method> resolveClass(Class<?> classToResolve)
    {
        Multimap<String, Method> result = ArrayListMultimap.create();
        
        for ( Method method : classToResolve.getMethods() )
        {
            ClickHandler annotation = method.getAnnotation(ClickHandler.class);
            
            if ( annotation == null )
            {
                continue;
            }
            
            if ( !checkMethodSignature(method) )
            {
                continue;
            }
            
            String name = annotation.value().isEmpty() ? method.getName() : annotation.value();
            
            method.setAccessible(true);
            result.put(name, method);
        }
        
        return result;
    }
    
    private boolean checkMethodSignature(Method method)
    {
        return method.getReturnType() == Void.TYPE && method.getParameters().length == 1 && method.getParameterTypes()[0] == ClickEvent.class;
    }
    
    public void callClickEvent(Gui gui, String clickHandlerName, ClickEvent event)
    {
        Preconditions.checkArgument(gui != null);
        Preconditions.checkArgument(event != null);
        
        Multimap<String, Method> methods = resolvedGuiClasses.computeIfAbsent(gui.getClass(), this::resolveClass);
        for ( Method method : methods.get(clickHandlerName) )
        {
            try
            {
                method.invoke(gui, event);
            }
            catch ( Throwable e )
            {
                SneakyThrow.sneaky(e);
            }
        }
    }
}
