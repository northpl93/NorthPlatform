package pl.north93.zgame.api.global.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetConfig
{
    /**
     * @return klasa configu.
     */
    Class<?> type();

    /**
     * @return unikalny identyfikator configu w sieci.
     */
    String id();
}
