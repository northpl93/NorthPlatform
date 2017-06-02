package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.AccessibleObject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

abstract class AbstractBeanContainer
{
    private final Class<?> type;
    private final String   name;

    public AbstractBeanContainer(final Class<?> type, final String name)
    {
        this.type = type;
        this.name = name;
    }

    public Class<?> getType()
    {
        return this.type;
    }

    public String getName()
    {
        return this.name;
    }

    public abstract Object getValue(AccessibleObject injectionContext);

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("name", this.name).toString();
    }
}
