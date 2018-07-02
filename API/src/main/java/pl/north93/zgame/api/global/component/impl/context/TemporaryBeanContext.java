package pl.north93.zgame.api.global.component.impl.context;

import pl.north93.zgame.api.global.component.impl.container.BeanFactory;
import pl.north93.zgame.api.global.component.impl.general.BeanQuery;

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

    public void putIfAbsent(final Class<?> type, final Object bean)
    {
        if (this.isBeanExists(type))
        {
            return;
        }

        this.put(type, bean);
    }

    public void put(final Class<?> type, final String name, final Object bean)
    {
        BeanFactory.INSTANCE.createStaticBeanManually(this, type, name, bean);
    }

    public void putIfAbsent(final Class<?> type, final String name, final Object bean)
    {
        final BeanQuery query = new BeanQuery().type(type).name(name).requireExactTypeMatch();
        if (this.isBeanExists(query))
        {
            return;
        }

        this.put(type, name, bean);
    }
}
