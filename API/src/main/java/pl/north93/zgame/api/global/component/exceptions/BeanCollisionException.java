package pl.north93.zgame.api.global.component.exceptions;

import static java.text.MessageFormat.format;


import pl.north93.zgame.api.global.component.impl.container.AbstractBeanContainer;

public class BeanCollisionException extends RuntimeException
{
    public BeanCollisionException(final AbstractBeanContainer beanContainer)
    {
        super(format("Bean with type {0} and name {1} already exists!", beanContainer.getType().getName(), beanContainer.getName()));
    }
}
