package pl.north93.zgame.api.global.component.impl.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MethodParameterInjectionContext implements IInjectionContext
{
    private final Parameter parameter;

    public MethodParameterInjectionContext(final Parameter parameter)
    {
        this.parameter = parameter;
    }

    @Override
    public Class<?> getDeclaringClass()
    {
        return this.parameter.getDeclaringExecutable().getDeclaringClass();
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return this.parameter.getAnnotations();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("parameter", this.parameter).toString();
    }
}
