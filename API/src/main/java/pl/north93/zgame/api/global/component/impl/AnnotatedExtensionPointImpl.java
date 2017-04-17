package pl.north93.zgame.api.global.component.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reflections.Reflections;

import pl.north93.zgame.api.global.component.IAnnotated;
import pl.north93.zgame.api.global.component.IAnnotatedExtensionPoint;
import pl.north93.zgame.api.global.component.IExtensionHandler;
import pl.north93.zgame.api.global.component.annotations.IgnoreExtensionPoint;
import pl.north93.zgame.api.global.utils.Wrapper;

class AnnotatedExtensionPointImpl implements IAnnotatedExtensionPoint
{
    private final Class<? extends Annotation> clazz;
    private final List<IAnnotated>            implementations;
    private final Wrapper<IExtensionHandler>  handler;

    public AnnotatedExtensionPointImpl(final Class<? extends Annotation> clazz)
    {
        this.clazz = clazz;
        this.implementations = new ArrayList<>(8);
        this.handler = new Wrapper<>();
    }

    @Override
    public Class<? extends Annotation> getExtensionPointClass()
    {
        return this.clazz;
    }

    @Override
    public List<IAnnotated> getImplementations()
    {
        return Collections.unmodifiableList(this.implementations);
    }

    @Override
    public void addImplementation(final Object rawImpl)
    {
        if (!(rawImpl instanceof Method || rawImpl instanceof Field))
        {
            return;
        }
        final AccessibleObject accessible = (AccessibleObject) rawImpl;
        final Annotation annotation = accessible.getAnnotation(this.clazz);
        if (annotation == null)
        {
            throw new IllegalArgumentException();
        }

        if (accessible.isAnnotationPresent(IgnoreExtensionPoint.class))
        {
            return;
        }

        final AnnotatedImpl annotated = new AnnotatedImpl(annotation, accessible);
        synchronized (this.handler)
        {
            this.implementations.add(annotated);

            final IExtensionHandler handler = this.handler.get();
            if (handler != null)
            {
                //noinspection unchecked
                handler.handle(annotated);
            }
        }
    }

    @Override
    public void setHandler(final IExtensionHandler handler)
    {
        synchronized (this.handler)
        {
            this.handler.set(handler);
            //noinspection unchecked
            this.implementations.forEach(handler::handle);
        }
    }

    @Override
    public void scan(final Reflections reflections)
    {
        final Class<? extends Annotation> annotationToSearch = this.getExtensionPointClass();

        final Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationToSearch);
        methods.forEach(this::addImplementation);

        // todo support variables
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).append("implementations", this.implementations).append("handler", this.handler).toString();
    }
}
