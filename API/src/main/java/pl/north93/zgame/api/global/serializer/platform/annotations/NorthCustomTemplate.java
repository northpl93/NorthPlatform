package pl.north93.zgame.api.global.serializer.platform.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.north93.zgame.api.global.serializer.platform.template.Template;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NorthCustomTemplate
{
    Class<? extends Template> value();
}
