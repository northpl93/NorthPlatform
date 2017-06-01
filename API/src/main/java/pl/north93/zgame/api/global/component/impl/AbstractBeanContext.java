package pl.north93.zgame.api.global.component.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pl.north93.zgame.api.global.component.IBeanContext;
import pl.north93.zgame.api.global.component.IBeanQuery;
import pl.north93.zgame.api.global.component.exceptions.BeanNotFoundException;

abstract class AbstractBeanContext implements IBeanContext
{
    private final AbstractBeanContext parent;
    private final String              name;
    private final List<BeanContainer> registeredBeans;

    public AbstractBeanContext(final AbstractBeanContext parent, final String name)
    {
        this.parent = parent;
        this.registeredBeans = new ArrayList<>();
        this.name = name;
    }

    public void add(final BeanContainer bean)
    {
        this.registeredBeans.add(bean);
    }

    @Override
    public IBeanContext getParent()
    {
        return this.parent;
    }

    @Override
    public String getBeanContextName()
    {
        return this.name;
    }

    public Collection<BeanContainer> getAll()
    {
        if (this.parent != null)
        {
            final Set<BeanContainer> beans = new HashSet<>();
            beans.addAll(this.parent.getAll());
            beans.addAll(this.registeredBeans);
            return beans;
        }
        return this.registeredBeans;
    }

    @Override
    public <T> T getBean(final IBeanQuery query)
    {
        //noinspection unchecked
        return (T) this.beanStream()
                       .filter((BeanQuery) query)
                       .reduce((u, v) -> { throw new IllegalStateException("More than one bean found. Use getBeans!"); })
                       .map(BeanContainer::getValue)
                       .orElseThrow(BeanNotFoundException::new);
    }

    @Override
    public <T> Collection<T> getBeans(final IBeanQuery query)
    {
        //noinspection unchecked
        return (Collection<T>) this.beanStream()
                                   .filter((BeanQuery) query)
                                   .map(BeanContainer::getValue)
                                   .collect(Collectors.toSet());
    }

    @Override
    public <T> Collection<T> getBeans(final Class<T> clazz)
    {
        return this.getBeans(new BeanQuery());
    }

    @Override
    public <T> Collection<T> getBeans(final String name)
    {
        return this.getBeans(new BeanQuery());
    }

    @Override
    public <T> T getBean(final Class<T> clazz)
    {
        return this.getBean(new BeanQuery().type(clazz));
    }

    @Override
    public <T> T getBean(final String beanName)
    {
        return this.getBean(new BeanQuery().name(beanName));
    }

    private Stream<BeanContainer> beanStream()
    {
        if (this.parent != null)
        {
            return Stream.concat(this.registeredBeans.stream(), this.parent.beanStream());
        }
        return this.registeredBeans.stream();
    }
}
