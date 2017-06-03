package pl.north93.zgame.api.global.component.exceptions;

import pl.north93.zgame.api.global.component.IBeanQuery;

public class BeanNotFoundException extends RuntimeException
{
    public BeanNotFoundException(final IBeanQuery beanQuery)
    {
        super("Not found bean with specified criteria: " + beanQuery);
    }
}
