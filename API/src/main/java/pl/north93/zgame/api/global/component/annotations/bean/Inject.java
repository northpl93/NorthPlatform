package pl.north93.zgame.api.global.component.annotations.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Inject
{
    /**
     * Ustawienie tej wartosci na true spowoduje zignorowanie
     * ewentualnych bledow podczas wczytywania i pozostawi
     * wartosc jako null. Wyjatek nie zostanie wyprintowany.
     * @return czy zignorowac blad wstrzykiwania.
     */
    boolean silentFail() default false;
}
