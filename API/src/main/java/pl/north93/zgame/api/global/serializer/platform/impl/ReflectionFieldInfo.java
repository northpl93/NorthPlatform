package pl.north93.zgame.api.global.serializer.platform.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lombok.ToString;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;

@ToString
/*default*/ final class ReflectionFieldInfo implements FieldInfo
{
    private final String name;
    private final Type   type;

    public ReflectionFieldInfo(final Field field)
    {
        this.name = field.getName();
        this.type = field.getGenericType();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Type getType()
    {
        return this.type;
    }
}
