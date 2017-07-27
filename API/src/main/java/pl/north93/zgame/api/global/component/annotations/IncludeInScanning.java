package pl.north93.zgame.api.global.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Liste paczek konfiguruje sie w components.yml
 * @see pl.north93.zgame.api.global.component.ComponentDescription#packages
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface IncludeInScanning
{
    String value();
}
