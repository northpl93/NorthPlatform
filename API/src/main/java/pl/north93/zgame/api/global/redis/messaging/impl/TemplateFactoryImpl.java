package pl.north93.zgame.api.global.redis.messaging.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.FieldUtils;

import org.diorite.utils.reflections.DioriteReflectionUtils;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateFactory;
import pl.north93.zgame.api.global.redis.messaging.TemplateGeneric;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackIgnore;
import pl.north93.zgame.api.global.redis.messaging.impl.element.ITemplateElement;
import pl.north93.zgame.api.global.redis.messaging.impl.element.TemplateElementFactory;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.EnumTemplate;

public class TemplateFactoryImpl implements TemplateFactory
{
    private final TemplateElementFactory templateElementFactory = new TemplateElementFactory(false); // TODO switch to true

    @Override
    public <T> Template<T> createTemplate(final TemplateManager templateManager, final Class<T> clazz)
    {
        if (clazz.isEnum()) // DynamicTemplate can invoke createTemplate with enum class
        {
            //noinspection unchecked
            return (Template<T>) new EnumTemplate(clazz);
        }

        final List<ITemplateElement> elements = new LinkedList<>();
        final Field[] fields = FieldUtils.getAllFields(clazz);

        for (final Field field : fields)
        {
            field.setAccessible(true);
            if (field.isAnnotationPresent(MsgPackIgnore.class) || Modifier.isStatic(field.getModifiers()))
            {
                continue; // Skip fields with MsgPackIgnore annotation
                          // Ignore static fields
            }

            final Class<?> fieldType = field.getType();
            final Type genericType = field.getGenericType();
            final Template<?> template;

            if (field.isAnnotationPresent(MsgPackCustomTemplate.class))
            {
                final MsgPackCustomTemplate annotation = field.getAnnotation(MsgPackCustomTemplate.class);
                final Class<?> templateClass = annotation.value();

                if (TemplateGeneric.class.isAssignableFrom(templateClass))
                {
                    template = ((TemplateGeneric) DioriteReflectionUtils.getConstructor(templateClass).invoke()).setGenericType((ParameterizedType) genericType);
                }
                else
                {
                    template = (Template) DioriteReflectionUtils.getConstructor(templateClass).invoke();
                }
            }
            else if (fieldType.isEnum()) // enum needs a own special template
            {
                template = new EnumTemplate(fieldType);
            }
            else if (fieldType.isPrimitive()) // we doesn't support primitives
            {
                throw new RuntimeException("Primitive values are unsupported. (" + fieldType + ")");
            }
            else if (fieldType.isArray()) // support arrays
            {
                final Class<?> typeNoArray = fieldType.getComponentType();
                template = new ArrayTemplate(typeNoArray, templateManager.getTemplate(typeNoArray));
            }
            else if (genericType instanceof ParameterizedType) // Get template with generic type
            {
                template = templateManager.getTemplate(fieldType, (ParameterizedType) genericType);
            }
            else
            {
                template = templateManager.getTemplate(fieldType);
            }

            elements.add(this.templateElementFactory.getTemplateElement(clazz, field, template));
        }

        return new TemplateImpl<>(clazz, elements);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("templateElementFactory", this.templateElementFactory).toString();
    }
}
