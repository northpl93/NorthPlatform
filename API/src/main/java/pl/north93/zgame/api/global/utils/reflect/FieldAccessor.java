package pl.north93.zgame.api.global.utils.reflect;

import java.lang.reflect.Field;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.global.utils.lang.SneakyThrow;

public class FieldAccessor
{
    private final Field field;
    private final Object instance;
    
    private FieldAccessor(Field field, Object instance)
    {
        this.field = field;
        this.instance = instance;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get()
    {
        try
        {
            return (T) field.get(instance);
        }
        catch ( Throwable e )
        {
            SneakyThrow.sneaky(e);
            return null;
        }
    }
    
    public <T> void set(T value)
    {
        try
        {
            field.set(instance, value);
        }
        catch ( Throwable e )
        {
            SneakyThrow.sneaky(e);
        }
    }
    
    public static FieldAccessor of(Class<?> clazz, String fieldName)
    {
        Preconditions.checkArgument(clazz != null);
        Preconditions.checkArgument(fieldName != null && !fieldName.isEmpty());
        
        try
        {
            return new FieldAccessor(findField(clazz, fieldName), null);
        }
        catch ( NoSuchFieldException e )
        {
            SneakyThrow.sneaky(e);
            return null;
        }
    }
    
    public static FieldAccessor of(Object instance, String fieldName)
    {
        Preconditions.checkArgument(instance != null);
        Preconditions.checkArgument(fieldName != null && !fieldName.isEmpty());
        
        try
        {
            return new FieldAccessor(findField(instance.getClass(), fieldName), instance);
        }
        catch ( NoSuchFieldException e )
        {
            SneakyThrow.sneaky(e);
            return null;
        }
    }
    
    private static Field findField(final Class<?> clazz, final String fieldName) throws NoSuchFieldException
    {
        Class<?> cls = clazz;
        
        while ( cls != null )
        {
            try
            {
                return clazz.getDeclaredField(fieldName);
            }
            catch ( NoSuchFieldException e )
            {
                cls = clazz.getSuperclass();
            }
        }
        
        throw new NoSuchFieldException(fieldName + " in " + clazz.getName());
    }
}