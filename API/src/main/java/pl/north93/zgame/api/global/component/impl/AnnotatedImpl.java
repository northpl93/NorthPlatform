package pl.north93.zgame.api.global.component.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.IAnnotated;

class AnnotatedImpl implements IAnnotated
{
    private Annotation annotation;
    private Object     element;

    public AnnotatedImpl(final Annotation annotation, final Object element)
    {
        this.annotation = annotation;
        this.element = element;
    }

    @Override
    public <T extends Annotation> T getAnnotation()
    {
        //noinspection unchecked
        return (T) this.annotation;
    }

    @Override
    public Object getElement()
    {
        return this.element;
    }

    @Override
    public boolean isMethod()
    {
        return this.element instanceof Method;
    }

    @Override
    public boolean isField()
    {
        return this.element instanceof Field;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("annotation", this.annotation).append("element", this.element).toString();
    }
}
