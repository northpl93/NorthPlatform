package pl.north93.zgame.api.global.redis.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timeout
{
    /**
     * The default method timeout is 1 second
     *
     * @return time after the RpcTimeoutException will be thrown
     */
    int value() default 1;
}
