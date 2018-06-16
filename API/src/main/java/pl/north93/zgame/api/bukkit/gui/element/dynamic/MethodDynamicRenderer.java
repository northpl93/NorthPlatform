package pl.north93.zgame.api.bukkit.gui.element.dynamic;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.utils.lang.SneakyThrow;

class MethodDynamicRenderer implements IDynamicRenderer
{
    private static final Map<Class<? extends Gui>, Map<String, MethodHandle>> methodHandlesCache = new HashMap<>(); // TODO: add way to clear this cache
    
    private final Gui gui;
    private final MethodHandle method;
    
    public MethodDynamicRenderer(Gui gui, String methodName)
    {
        this.gui = gui;
        this.method = findMethod(gui.getClass(), methodName);
        
        Preconditions.checkState(method != null);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Collection<DynamicElementData> render()
    {
        return (Collection<DynamicElementData>) SneakyThrow.sneaky(() -> method.invoke(gui));
    }
    
    private static MethodHandle findMethod(Class<? extends Gui> guiClass, String name)
    {
        Map<String, MethodHandle> methods = methodHandlesCache.computeIfAbsent(guiClass, cls -> SneakyThrow.sneaky(() -> findMethods(cls)));
        return methods.get(name);
    }
    
    private static Map<String, MethodHandle> findMethods(Class<? extends Gui> guiClass) throws Exception
    {
        Map<String, MethodHandle> result = new HashMap<>();
        
        Class<?> cls = guiClass;
        
        while ( cls != null )
        {
            for ( Method m : cls.getMethods() )
            {
                DynamicRenderer annotation = m.getAnnotation(DynamicRenderer.class);
                if ( annotation == null )
                {
                    continue;
                }
                
                String name = annotation.value();
                
                if ( name.isEmpty() )
                {
                    name = m.getName();
                }
                
                m.setAccessible(true);
                MethodHandle handle = MethodHandles.publicLookup().unreflect(m).asType(MethodType.methodType(Collection.class, Gui.class));
                
                result.put(name, handle);
            }
            
            cls = cls.getSuperclass();
        }
        
        return result;
    }
}
