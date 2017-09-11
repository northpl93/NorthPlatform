package pl.arieals.minigame.goldhunter;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface GoldHunterLogger
{
    String value() default "";
    
    boolean useToString() default false;
}
