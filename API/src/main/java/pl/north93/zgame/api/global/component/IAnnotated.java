package pl.north93.zgame.api.global.component;

import java.lang.annotation.Annotation;

public interface IAnnotated
{
    <T extends Annotation> T getAnnotation();

    Object getElement();

    boolean isMethod();

    boolean isField();
}
