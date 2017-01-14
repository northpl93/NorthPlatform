package pl.north93.zgame.api.global.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Wrapper<T>
{
    private T obj;

    public Wrapper()
    {
    }

    public Wrapper(final T obj)
    {
        this.obj = obj;
    }

    public T get()
    {
        return this.obj;
    }

    public void set(final T obj)
    {
        this.obj = obj;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("obj", this.obj).toString();
    }
}
