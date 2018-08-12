package pl.north93.zgame.api.global.serializer.platform.impl;

import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;


import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.FieldUtils;

import pl.north93.zgame.api.global.serializer.platform.TemplateFactory;
import pl.north93.zgame.api.global.serializer.platform.annotations.NorthTransient;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.ITemplateElement;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

/*default*/ class TemplateFactoryImpl implements TemplateFactory
{
    private final TemplateElementFactory templateElementFactory = new TemplateElementFactory(false); // TODO switch to true

    @Override
    public <T> Template<T, SerializationContext, DeserializationContext> createTemplate(final TemplateEngine templateEngine, final Class<T> clazz)
    {
        final List<ITemplateElement> elements = new LinkedList<>();
        final Field[] fields = FieldUtils.getAllFields(clazz);

        for (final Field field : fields)
        {
            field.setAccessible(true);
            if (this.shouldSkipField(field))
            {
                continue;
            }

            /*final Class<?> fieldType = field.getType();
            final Type genericType = field.getGenericType();
            final Template<?> template;

            if (fieldType.isPrimitive()) // we doesn't support primitives
            {
                throw new RuntimeException("Primitive values are unsupported. (" + fieldType + ")");
            }
            else if (fieldType.isArray()) // support arrays
            {
                if (byte[].class == fieldType)
                {
                    template = new ByteArrayTemplate(); // support byte[] fields for performance
                }
                else
                {
                    final Class<?> typeNoArray = fieldType.getComponentType();
                    template = new ArrayTemplate(typeNoArray, templateManager.getTemplate(typeNoArray));
                }
            }
            else if (genericType instanceof ParameterizedType) // Get template with generic type
            {
                template = templateManager.getTemplate(fieldType, (ParameterizedType) genericType);
            }
            else
            {
                template = templateManager.getTemplate(fieldType);
            }*/

            final Template<Object, SerializationContext, DeserializationContext> template = templateEngine.getTemplate(field.getGenericType());
            elements.add(this.templateElementFactory.getTemplateElement(clazz, field, template));
        }

        return new TemplateImpl<>(clazz, elements);
    }

    private boolean shouldSkipField(final Field field)
    {
        // Skip fields with NorthTransient annotation
        // Ignore static and transient fields
        final int modifiers = field.getModifiers();
        return field.isAnnotationPresent(NorthTransient.class) || isTransient(modifiers) || isStatic(modifiers);
    }

    /*@SuppressWarnings("unchecked")
    private <T> Template<T> handleEnum(final Class<T> clazz)
    {
        final Class<?> enumClass;
        if (clazz.getSuperclass() == Enum.class)
        {
            enumClass = clazz;
        }
        else
        {
            enumClass = clazz.getSuperclass();
        }

        return (Template<T>) new EnumTemplate(enumClass);
    }*/

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("templateElementFactory", this.templateElementFactory).toString();
    }
}
