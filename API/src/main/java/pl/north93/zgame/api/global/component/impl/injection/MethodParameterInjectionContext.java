package pl.north93.zgame.api.global.component.impl.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MethodParameterInjectionContext implements IInjectionContext
{
    private final Object instance;
    private final Parameter parameter;

    public MethodParameterInjectionContext(final Object instance, final Parameter parameter)
    {
        this.instance = instance;
        this.parameter = parameter;
    }

    @Override
    public Class<?> getDeclaringClass()
    {
        return this.parameter.getDeclaringExecutable().getDeclaringClass();
    }

    @Override
    public Object getInstance()
    {
        return this.instance;
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return this.parameter.getAnnotations();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("instance", this.instance).append("parameter", this.parameter).toString();
    }
}
