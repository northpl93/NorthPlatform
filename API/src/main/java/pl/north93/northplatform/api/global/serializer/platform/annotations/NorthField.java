package pl.north93.northplatform.api.global.serializer.platform.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NorthField
{
    String name() default Default.DEFAULT_STRING;

    Class<?> type() default Default.class;

    final class Default
    {
        public static final String DEFAULT_STRING = "$DEFAULT";
    }
}
