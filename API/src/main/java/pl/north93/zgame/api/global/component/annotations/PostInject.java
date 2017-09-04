package pl.north93.zgame.api.global.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wstrzykuje wykonanie metody na koniec konstruktora.
 *
 * public Klasa() {
 *     // WSTRZYKNIETY kod injectora
 *
 *     // kod konstruktora
 *
 *     // WSTRZYKNIETE wywolania metod postInject
 *     this.metoda();
 * }
 *
 * @deprecated Injector jest teraz wstrzykiwany na poczatek konstruktora,
 *             metody @PostInject sa teraz zbedne.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Deprecated
public @interface PostInject
{
}
