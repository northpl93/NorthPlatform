package pl.north93.zgame.api.global.component.impl;

class TemporaryBeanContext extends AbstractBeanContext
{
    public TemporaryBeanContext(final AbstractBeanContext parent, final String name)
    {
        super(parent, name);
    }

    /*default*/ void put(final Class<?> type, final Object bean)
    {
        this.add(new StaticBeanContainer(type, type.getName(), bean));
    }

    /*default*/ void put(final Class<?> type, final String name, final Object bean)
    {
        this.add(new StaticBeanContainer(type, name, bean));
    }
}
