package pl.north93.zgame.api.global.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Inject
{
    /**
     * Opcjonalna nazwa beana do wstrzyknięcia.
     * Jeśli nie podana wstrzykiwanie będzie oparte o typ.
     *
     * @return nazwa beana.
     */
    String name() default "";
}
