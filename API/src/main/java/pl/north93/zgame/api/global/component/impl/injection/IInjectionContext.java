package pl.north93.zgame.api.global.component.impl.injection;

import java.lang.annotation.Annotation;

public interface IInjectionContext
{
    Class<?> getDeclaringClass();

    Annotation[] getAnnotations();
}
