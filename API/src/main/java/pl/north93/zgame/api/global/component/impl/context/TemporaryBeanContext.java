package pl.north93.zgame.api.global.component.impl.context;

import pl.north93.zgame.api.global.component.impl.container.BeanFactory;

public class TemporaryBeanContext extends AbstractBeanContext
{
    public TemporaryBeanContext(final AbstractBeanContext parent)
    {
        super(parent, "temp");
    }

    public void put(final Class<?> type, final Object bean)
    {
        BeanFactory.INSTANCE.createStaticBeanManually(this, type, type.getName(), bean);
    }

    public void put(final Class<?> type, final String name, final Object bean)
    {
        BeanFactory.INSTANCE.createStaticBeanManually(this, type, name, bean);
    }
}
