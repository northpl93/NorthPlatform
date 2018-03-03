package pl.north93.zgame.api.global.utils.reflect;

import java.lang.reflect.Field;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.global.utils.lang.SneakyThrow;

public class FieldAccessor <T>
{
    private final Field field;
    private final Object instance;
    
    private FieldAccessor(Field field, Object instance)
    {
        this.field = field;
        this.instance = instance;
    }
    
    @SuppressWarnings("unchecked")
    public T get()
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
    
    public void set(T value)
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
    
    public FieldAccessor<T> withInstance(Object instance)
    {
        return new FieldAccessor<>(field, instance);
    }
    
    static <T> FieldAccessor<T> fromClassAndFieldName(Class<?> clazz, String fieldName)
    {
        Preconditions.checkArgument(clazz != null);
        Preconditions.checkArgument(fieldName != null && !fieldName.isEmpty());
        
        try
        {
            Field field = findField(clazz, fieldName);
            field.setAccessible(true);
            return new FieldAccessor<>(field, null);
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
