package pl.north93.zgame.api.global.messages;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Messages
{
    String value();
}
