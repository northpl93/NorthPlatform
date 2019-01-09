package pl.north93.northplatform.antycheat.timeline;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class DataKey<T>
{
    private final String                  name;
    private final Function<PlayerData, T> creator;

    public DataKey(final String name, final Function<PlayerData, T> creator)
    {
        this.name = name;
        this.creator = creator;
    }

    public DataKey(final String name, final Supplier<T> creator)
    {
        this(name, data -> creator.get());
    }

    public String getName()
    {
        return this.name;
    }

    public Function<PlayerData, T> getCreator()
    {
        return this.creator;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }
        final DataKey<?> dataKey = (DataKey<?>) o;
        return Objects.equals(this.name, dataKey.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.name);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).toString();
    }
}
