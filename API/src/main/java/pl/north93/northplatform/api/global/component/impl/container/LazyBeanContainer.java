package pl.north93.northplatform.api.global.component.impl.container;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.lazy.LazyValue;

import pl.north93.northplatform.api.global.component.impl.injection.IInjectionContext;

class LazyBeanContainer extends AbstractBeanContainer
{
    private final LazyValue value;

    public LazyBeanContainer(final Class<?> type, final String name, final LazyValue value)
    {
        super(type, name);
        this.value = value;
    }

    @Override
    public Object getValue(final IInjectionContext injectionContext)
    {
        return this.value.get();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("value", this.value).toString();
    }
}
