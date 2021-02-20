package pl.north93.northplatform.api.global.component.annotations.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Profile
{
    /**
     * @return Name of the profile.
     */
    String value();

    /**
     * @return If this option is set to true, then the unexisting profile should be treated as a disabled profile.
     */
    boolean allowUnexistingProfile() default true;
}
