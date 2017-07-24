package pl.north93.zgame.api.global.component.impl.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FieldInjectionContext implements IInjectionContext
{
    private final Field field;

    public FieldInjectionContext(final Field field)
    {
        this.field = field;
    }

    @Override
    public Class<?> getDeclaringClass()
    {
        return this.field.getDeclaringClass();
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return this.field.getAnnotations();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("field", this.field).toString();
    }
}
