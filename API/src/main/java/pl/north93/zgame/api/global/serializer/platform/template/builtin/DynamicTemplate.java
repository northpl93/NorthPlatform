package pl.north93.zgame.api.global.serializer.platform.template.builtin;

import static sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl.make;


import java.lang.reflect.Type;

import pl.north93.zgame.api.global.serializer.platform.CustomFieldInfo;
import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateFilter;
import pl.north93.zgame.api.global.serializer.platform.template.TemplatePriority;

public class DynamicTemplate implements Template<Object, SerializationContext, DeserializationContext>
{
    private static final FieldInfo FIELD_TYPE = new CustomFieldInfo("type", String.class);

    public static final class DynamicTemplateFilter implements TemplateFilter
    {
        @Override
        public int getPriority()
        {
            return TemplatePriority.HIGHEST;
        }

        @Override
        public boolean isApplicableTo(final TemplateEngine templateEngine, final Type type)
        {
            return templateEngine.isNeedsDynamicResolution(type);
        }
    }

    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Object object) throws Exception
    {
        context.enterObject(field);
        try
        {
            final Class<?> objectClass = object.getClass();
            context.writeString(FIELD_TYPE, objectClass.getName());

            final Type fixedType = this.fixType(context.getTemplateEngine(), field.getType(), objectClass);

            final Template<Object, SerializationContext, DeserializationContext> template = context.getTemplateEngine().getTemplate(fixedType);
            template.serialise(context, this.getValueField(fixedType), object);
        }
        finally
        {
            context.exitObject(field);
        }
    }

    @Override
    public Object deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        final TemplateEngine templateEngine = context.getTemplateEngine();
        context.enterObject(field);
        try
        {
            final String className = context.readString(FIELD_TYPE);
            final Class<?> templateClass = templateEngine.findClass(className);

            final Type fixedType = this.fixType(templateEngine, field.getType(), templateClass);

            final Template<Object, SerializationContext, DeserializationContext> template = templateEngine.getTemplate(fixedType);
            return template.deserialize(context, this.getValueField(fixedType));
        }
        finally
        {
            context.exitObject(field);
        }
    }

    // jesli posiadamy informacje o generycznym typie to go dodajemy;
    // w przeciwnym wypadku nie powodujemy bledu
    private Type fixType(final TemplateEngine templateEngine, final Type fieldType, final Class templateClass)
    {
        final Type[] typeParameters = templateEngine.getTypeParameters(fieldType); // generic type
        return typeParameters.length == 0 ? templateClass : make(templateClass, typeParameters, null);
    }

    private FieldInfo getValueField(final Type type)
    {
        return new CustomFieldInfo("value", type);
    }
}
