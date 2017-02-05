package pl.north93.zgame.api.global.component.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.IExtensionHandler;
import pl.north93.zgame.api.global.component.IExtensionPoint;
import pl.north93.zgame.api.global.utils.Wrapper;

class ExtensionPointImpl<T> implements IExtensionPoint<T>
{
    private final Class<T> clazz;
    private final List<T>  implementations;
    private final Wrapper<IExtensionHandler<T>> handler;

    public ExtensionPointImpl(final Class<T> clazz)
    {
        this.clazz = clazz;
        this.implementations = new ArrayList<>(8);
        this.handler = new Wrapper<>();
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

    @Override
    public void addImplementation(final Object rawImpl)
    {
        final Class<?> implClass = rawImpl.getClass();
        if (! this.clazz.isAssignableFrom(implClass))
        {
            throw new IllegalArgumentException(implClass.getSimpleName() + " doesn't implement " + this.clazz.getSimpleName());
        }

        @SuppressWarnings("unchecked")
        final T impl = (T) rawImpl;

        synchronized (this.handler)
        {
            this.implementations.add(impl);

            final IExtensionHandler<T> handler = this.handler.get();
            if (handler != null)
            {
                handler.handle(impl);
            }
        }
    }

    @Override
    public void setHandler(final IExtensionHandler<T> handler)
    {
        synchronized (this.handler)
        {
            this.handler.set(handler);
            this.implementations.forEach(handler::handle);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).append("implementations", this.implementations).append("handler", this.handler).toString();
    }
}
