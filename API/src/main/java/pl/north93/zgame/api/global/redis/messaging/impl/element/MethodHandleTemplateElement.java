package pl.north93.zgame.api.global.redis.messaging.impl.element;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.messaging.Template;

class MethodHandleTemplateElement implements ITemplateElement
{
    private static final Lookup     LOOKUP      = MethodHandles.lookup();
    private static final MethodType GETTER_TYPE = MethodType.methodType(Object.class, Object.class);
    private static final MethodType SETTER_TYPE = MethodType.methodType(void.class, Object.class, Object.class);
    private final MethodHandle getter;
    private final MethodHandle setter;
    private final Template     template;

    public MethodHandleTemplateElement(final Field field, final Template template)
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
        this.template = template;
    }

    @Override
    public Object get(final Object instance)
    {
        try
        {
            return this.getter.invokeExact(instance);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException("Something went wrong while getting field...", throwable);
        }
    }

    @Override
    public void set(final Object instance, final Object value)
    {
        try
        {
            this.setter.invokeExact(instance, value);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException("Something went wrong while setting field...", throwable);
        }
    }

    @Override
    public Template getTemplate()
    {
        return this.template;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("getter", this.getter).append("setter", this.setter).append("template", this.template).toString();
    }
}
