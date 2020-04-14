package pl.north93.northplatform.api.global.utils.lang;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MethodHandlesUtils
{
    // TODO: make better reflection utils
    
    public static MethodHandle unreflectGetter(Class<?> clazz, String fieldName)
    {
        return unreflectGetter(clazz, fieldName, true);
    }

    @SneakyThrows({Exception.class})
    public static MethodHandle unreflectGetter(Class<?> clazz, String fieldName, boolean forceAccess)
    {
        Field field = findField(clazz, fieldName, forceAccess);
        return MethodHandles.lookup().unreflectGetter(field);
    }
    
    public static MethodHandle unreflectSetter(Class<?> clazz, String fieldName)
    {
        return unreflectSetter(clazz, fieldName, true);
    }

    @SneakyThrows({Exception.class})
    public static MethodHandle unreflectSetter(Class<?> clazz, String fieldName, boolean forceAccess)
    {
        Field field = findField(clazz, fieldName, forceAccess);
        return MethodHandles.lookup().unreflectSetter(field);
    }
    
    private static Field findField(Class<?> clazz, String fieldName, boolean forceAccess) throws Exception
    {
        Class<?> cls = clazz;
        while ( cls != null )
        {
            try
            {
                Field field = cls.getDeclaredField(fieldName);
                field.setAccessible(forceAccess);
                return field;
            }
            catch ( NoSuchFieldException e )
            {
                cls = clazz.getSuperclass();
            }
        }
        
        throw new NoSuchFieldException("Couldn't find field " + clazz.getName() + "#" + fieldName);
    }
}
