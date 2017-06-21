package pl.north93.zgame.api.global.component.exceptions;

public class BeanCreationException extends RuntimeException
{
    private final Class<?> beanClass;

    public BeanCreationException(final Class<?> beanClass, final Throwable cause)
    {
        super("Failed to create bean of type " + beanClass.getName(), cause);
        this.beanClass = beanClass;
    }

    public BeanCreationException(final Class<?> beanClass)
    {
        this(beanClass, null);
    }

    public Class<?> getBeanClass()
    {
        return this.beanClass;
    }
}
