package pl.north93.zgame.api.bukkit.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PacketHandler
{
    /**
     * priority of this handler
     */
    HandlerPriority priority() default HandlerPriority.NORMAL;
    
    /**
     * when true the handler will be executed in main server thread.
     */
    boolean sync() default false;
}
