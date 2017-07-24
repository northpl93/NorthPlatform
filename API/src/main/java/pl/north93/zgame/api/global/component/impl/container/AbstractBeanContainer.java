package pl.north93.zgame.api.global.component.impl.container;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.impl.injection.IInjectionContext;

public abstract class AbstractBeanContainer
{
    private final Class<?> type;
    private final String   name;

    public AbstractBeanContainer(final Class<?> type, final String name)
    {
        this.type = type;
        this.name = name;
    }

    public final Class<?> getType()
    {
        return this.type;
    }

    public final String getName()
    {
        return this.name;
    }

    public abstract Object getValue(IInjectionContext injectionContext);

    @Override
    public final boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final AbstractBeanContainer that = (AbstractBeanContainer) o;

        return this.type.equals(that.type) && this.name.equals(that.name);
    }

    @Override
    public final int hashCode()
    {
        int result = this.type.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("name", this.name).toString();
    }
}
