package pl.north93.northplatform.minigame.goldhunter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface GoldHunterLogger
{
    String value() default "";
    
    boolean useToString() default false;
}
