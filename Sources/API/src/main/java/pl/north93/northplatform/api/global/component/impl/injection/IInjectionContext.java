package pl.north93.northplatform.api.global.component.impl.injection;

import java.lang.annotation.Annotation;

/**
 * Represents a context of injection.
 * For example "field in a class", "method parameter".
 */
public interface IInjectionContext
{
    /**
     * @return Class in which injection context is.
     */
    Class<?> getDeclaringClass();

    /**
     * An instance of an object in which injection is being performed.
     * May be null if the injection is performed in a static context.
     *
     * @return instance of an object in which injection is being performed.
     */
    Object getInstance();

    /**
     * @return Array of annotations on a thing being injected.
     */
    Annotation[] getAnnotations();
}
