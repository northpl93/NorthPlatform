package pl.north93.zgame.api.global.component.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class BeanContainer
{
    private final Class<?> type;
    private final String   name;
    private final Object   value;

    public BeanContainer(final Class<?> type, final String name, final Object value)
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Class<?> getType()
    {
        return this.type;
    }

    public String getName()
    {
        return this.name;
    }

    public Object getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("name", this.name).append("value", this.value).toString();
    }
}
