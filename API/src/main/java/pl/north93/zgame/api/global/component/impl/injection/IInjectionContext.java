package pl.north93.zgame.api.global.component.impl.injection;

import java.lang.annotation.Annotation;

public interface IInjectionContext
{
    Class<?> getDeclaringClass();

    /**
     * Instancja obiektu w ktorej wstrzykujemy.
     * Moze byc nullem jesli kontekst jest statyczny.
     *
     * @return instancja obiektu w ktorym przeprowadzamy wstrzykiwanie.
     */
    Object getInstance();

    Annotation[] getAnnotations();
}
