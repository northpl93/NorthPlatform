package pl.north93.zgame.api.global.serializer.platform.template.builtin;

import java.lang.reflect.Type;

import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateFilter;

public class EnumTemplate implements Template<Enum<?>, SerializationContext, DeserializationContext>
{
    public static final class EnumTemplateFilter implements TemplateFilter
    {
        @Override
        public int getPriority()
        {
            return 10;
        }

        @Override
        public boolean isApplicableTo(final TemplateEngine templateEngine, final Type type)
        {
            if (type instanceof Class)
            {
                final Class clazz = (Class) type;
                if (clazz.isEnum())
                {
                    return true;
                }
                else if (clazz.getSuperclass() != null)
                {
                    // jesli implementujemy w enumie metody to wtedy powstaje klaa rozszerzajaca enum
                    return clazz.getSuperclass().isEnum();
                }

                return false;
            }
            return false;
        }
    }

    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Enum<?> object) throws Exception
    {
        context.writeString(field, object.name());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enum<?> deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        final String enumName = context.readString(field);

        final Class<Enum> enumClass = (Class<Enum>) this.getClassOfType(field.getType());
        return this.getEnumValue(enumClass, enumName);
    }

    private Class<?> getClassOfType(final Type type)
    {
        if (type instanceof Class)
        {
            return (Class<?>) type;
        }

        throw new IllegalArgumentException(type.getTypeName());
    }

    @SuppressWarnings("unchecked")
    private Enum<?> getEnumValue(final Class<?> enumClass, final String enumName)
    {
        if (enumClass.isEnum())
        {
            return Enum.valueOf((Class<Enum>) enumClass, enumName);
        }

        return Enum.valueOf((Class<Enum>) enumClass.getSuperclass(), enumName);
    }
}
