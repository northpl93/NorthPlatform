package pl.north93.zgame.api.global.component.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.IExtensionHandler;
import pl.north93.zgame.api.global.component.IExtensionPoint;

class ExtensionPointImpl<T> implements IExtensionPoint<T>
{
    private final Class<T> clazz;
    private final List<T>  implementations;
    private IExtensionHandler<T> handler;

    public ExtensionPointImpl(final Class<T> clazz)
    {
        this.clazz = clazz;
        this.implementations = new ArrayList<>(8);
    }

    @Override
    public Class<T> getExtensionPointClass()
    {
        return this.clazz;
    }

    @Override
    public List<T> getImplementations()
    {
        return Collections.unmodifiableList(this.implementations);
    }

    @SuppressWarnings("unchecked") // TODO check and fancy error?
    @Override
    public void addImplementation(final Object impl)
    {
        this.implementations.add((T) impl);
        if (this.handler != null)
        {
            this.handler.handle((T) impl);
        }
    }

    @Override
    public void setHandler(final IExtensionHandler<T> handler)
    {
        this.handler = handler;
        this.implementations.forEach(handler::handle);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).append("implementations", this.implementations).append("handler", this.handler).toString();
    }
}
