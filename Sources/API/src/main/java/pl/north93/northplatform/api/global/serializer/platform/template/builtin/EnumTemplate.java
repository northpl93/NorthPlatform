package pl.north93.northplatform.api.global.serializer.platform.template.builtin;

import java.lang.reflect.Type;

import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.northplatform.api.global.serializer.platform.context.SerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplateEngine;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplateFilter;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplatePriority;

public class EnumTemplate implements Template<Enum<?>, SerializationContext, DeserializationContext>
{
    public static final class EnumTemplateFilter implements TemplateFilter
    {
        @Override
        public int getPriority()
        {
            return TemplatePriority.HIGHEST;
        }

        @Override
        public boolean isApplicableTo(final TemplateEngine templateEngine, final Type type)
        {
            final Class<?> clazz = templateEngine.getRawClassFromType(type);
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

        final Class<Enum> enumClass = (Class<Enum>) context.getTemplateEngine().getRawClassFromType(field.getType());
        return this.getEnumValue(enumClass, enumName);
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
