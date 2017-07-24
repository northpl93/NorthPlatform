package pl.north93.zgame.api.bukkit.gui.impl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import org.spigotmc.SneakyThrow;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pl.north93.zgame.api.bukkit.gui.ClickHandler;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.uri.IUriManager;
import pl.north93.zgame.api.global.utils.Vars;

public class ClickHandlerManager<T extends ClickEvent>
{
    private final Class<T> clickEventClass;
    private final Map<Class<?>, Multimap<String, Method>> resolvedClasses = new WeakHashMap<>();
    @Inject
    private IUriManager uriManager;
    
    public ClickHandlerManager(Class<T> clickEventClass)
    {
        this.clickEventClass = clickEventClass;
    }
    
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
        return method.getReturnType() == Void.TYPE && method.getParameters().length == 1 && method.getParameterTypes()[0] == clickEventClass;
    }
    
    public void callClickEvent(IClickHandler handler, IClickable clickedElement, T event)
    {
        Preconditions.checkArgument(handler != null);
        Preconditions.checkArgument(clickedElement != null);
        Preconditions.checkArgument(event != null);
        
        for ( String handlerName : clickedElement.getClickHandlers() )
        {
            callClickEvent(handler, handlerName, event);
        }
    }
    
    public void callClickEvent(IClickHandler handler, String clickHandlerName, T event)
    {
        Preconditions.checkArgument(handler != null);
        Preconditions.checkArgument(clickHandlerName != null);
        Preconditions.checkArgument(event != null);

        if (clickHandlerName.startsWith("northplatform://"))
        {
            this.callNorthUriClickEvent(handler.getVariables(), clickHandlerName, event);
        }
        else
        {
            this.callMethodClickEvent(handler, clickHandlerName, event);
        }
    }

    private void callMethodClickEvent(IClickHandler handler, String clickHandlerName, T event)
    {
        Multimap<String, Method> methods = resolvedClasses.computeIfAbsent(handler.getClass(), this::resolveClass);
        for ( Method method : methods.get(clickHandlerName) )
        {
            try
            {
                method.invoke(handler, event);
            }
            catch ( Throwable e )
            {
                SneakyThrow.sneaky(e);
            }
        }
    }

    private void callNorthUriClickEvent(final Vars<Object> vars, final String clickHandlerName, final T event)
    {
        final Vars<Object> context = Vars.of("$playerId", (Object) event.getWhoClicked().getUniqueId())
                                         .and("$playerName", event.getWhoClicked().getName())
                                         .and(vars);

        NorthUriUtils.getInstance().call(clickHandlerName, context);
    }
}
