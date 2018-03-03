package pl.north93.zgame.api.global.utils.reflect;

public class Accessors
{
    private Accessors()
    {
    }
    
    public static <T> FieldAccessor<T> field(Class<?> clazz, String fieldName)
    {
        return FieldAccessor.fromClassAndFieldName(clazz, fieldName);
    }
    
    public static <T> FieldAccessor<T> fieldWithInstance(Object instance, String fieldName)
    {
        return FieldAccessor.<T>fromClassAndFieldName(instance.getClass(), fieldName).withInstance(instance);
    }
    
    // TODO: add more accessors e.g.: method accessors
}
