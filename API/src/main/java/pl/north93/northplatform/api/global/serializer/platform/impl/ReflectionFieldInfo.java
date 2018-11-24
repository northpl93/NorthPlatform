package pl.north93.northplatform.api.global.serializer.platform.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

import lombok.ToString;
import pl.north93.northplatform.api.global.serializer.platform.annotations.NorthField;
import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;

@ToString
/*default*/ final class ReflectionFieldInfo implements FieldInfo
{
    private final String name;
    private final Type   type;

    public ReflectionFieldInfo(final Field field)
    {
        final Optional<NorthField> customizations = Optional.ofNullable(field.getAnnotation(NorthField.class));

        this.name = computeName(field, customizations);
        this.type = computeType(field, customizations);
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

    private static String computeName(final Field field, final Optional<NorthField> customizations)
    {
        return customizations.map(_customizations ->
        {
            final String customName = _customizations.name();
            if (customName.equals(NorthField.Default.DEFAULT_STRING))
            {
                return field.getName();
            }

            return customName;
        }).orElseGet(field::getName);
    }

    private static Type computeType(final Field field, final Optional<NorthField> customizations)
    {
        return customizations.map(_customizations ->
        {
            final Class<?> customType = _customizations.type();
            if (customType.equals(NorthField.Default.class))
            {
                return field.getGenericType();
            }

            return customType;
        }).orElseGet(field::getGenericType);
    }
}
