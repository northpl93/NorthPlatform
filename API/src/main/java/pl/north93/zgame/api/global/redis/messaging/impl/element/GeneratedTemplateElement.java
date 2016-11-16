package pl.north93.zgame.api.global.redis.messaging.impl.element;

import java.lang.reflect.Field;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackNullable;

abstract class GeneratedTemplateElement implements ITemplateElement
{
    private final Template template;
    private final boolean  isNullable;

    public GeneratedTemplateElement(final Field field, final Template template)
    {
        this.isNullable = field.isAnnotationPresent(MsgPackNullable.class);
        this.template = template;
    }

    @Override
    public abstract Object get(final Object instance);

    @Override
    public abstract void set(final Object instance, final Object value);

    @Override
    public Template getTemplate()
    {
        return this.template;
    }

    @Override
    public boolean isNullable()
    {
        return this.isNullable;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("template", this.template).append("isNullable", this.isNullable).toString();
    }
}
