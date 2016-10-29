package pl.north93.zgame.api.global.redis.messaging.impl;

import javax.annotation.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.messaging.Template;

/**
 * Reprezentuje jedną zmienną w templatce.
 * Wszystkie wygenerowane templatki składają się z tych elementów.
 */
public class TemplateElement
{
    private static final Lookup     LOOKUP      = MethodHandles.lookup();
    private static final MethodType GETTER_TYPE = MethodType.methodType(Object.class, Object.class);
    private static final MethodType SETTER_TYPE = MethodType.methodType(void.class, Object.class, Object.class);
    private final MethodHandle getter;
    private final MethodHandle setter;
    private final Template template;
    private final boolean  isNullable;

    public TemplateElement(final Field field, final Template template)
    {
        try
        {
            this.getter = LOOKUP.unreflectGetter(field).asType(GETTER_TYPE);
            this.setter = LOOKUP.unreflectSetter(field).asType(SETTER_TYPE);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect getter or setter.", e);
        }
        this.isNullable = field.isAnnotationPresent(Nullable.class);
        this.template = template;
    }

    public MethodHandle getGetter()
    {
        return this.getter;
    }

    public MethodHandle getSetter()
    {
        return this.setter;
    }

    public Template getTemplate()
    {
        return this.template;
    }

    public boolean isNullable()
    {
        return this.isNullable;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("getter", this.getter).append("setter", this.setter).append("template", this.template).append("isNullable", this.isNullable).toString();
    }
}
