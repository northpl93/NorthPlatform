package pl.north93.zgame.api.global.component.impl.container;

import java.lang.reflect.AccessibleObject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class StaticBeanContainer extends AbstractBeanContainer
{
    private final Object value;

    public StaticBeanContainer(final Class<?> type, final String name, final Object value)
    {
        super(type, name);
        this.value = value;
    }

    @Override
    public Object getValue(final AccessibleObject injectionContext)
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("value", this.value).toString();
    }
}
