package pl.north93.zgame.api.global.redis.messaging.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.north93.zgame.api.global.redis.messaging.Template;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MsgPackCustomTemplate
{
    Class<? extends Template> value();
}
