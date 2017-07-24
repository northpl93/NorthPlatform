package pl.north93.zgame.api.global.component.impl.container;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.impl.injection.IInjectionContext;

class StaticBeanContainer extends AbstractBeanContainer
{
    private final Object value;

    public StaticBeanContainer(final Class<?> type, final String name, final Object value)
    {
        super(type, name);
        this.value = value;
    }

    @Override
    public Object getValue(final IInjectionContext injectionContext)
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("value", this.value).toString();
    }
}
