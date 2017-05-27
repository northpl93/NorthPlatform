package pl.north93.zgame.api.global.component.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pl.north93.zgame.api.global.component.IBeanContext;
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
    public IBeanContext getParent()
    {
        return this.parent;
    }

    @Override
    public String getBeanContextName()
    {
        return this.name;
    }

    @Override
    public <T> T getBean(final Class<T> clazz)
    {
        final Collection<T> beans = this.getBeans(clazz);
        if (beans.size() < 2)
        {
            return beans.stream().findFirst().orElseThrow(BeanNotFoundException::new);
        }
        return null;
    }

    @Override
    public <T> T getBean(final String beanName)
    {
        final Collection<T> beans = this.getBeans(beanName);
        if (beans.size() < 2)
        {
            return beans.stream().findFirst().orElseThrow(BeanNotFoundException::new);
        }
        return null;
    }

    private Stream<BeanContainer> beanStream()
    {
        if (this.parent != null)
        {
            return Stream.concat(this.registeredBeans.stream(), this.parent.beanStream());
        }
        return this.registeredBeans.stream();
    }

    @Override
    public <T> Collection<T> getBeans(final Class<T> clazz)
    {
        //noinspection unchecked
        return (Collection<T>) this.beanStream().filter(bean -> bean.getType().equals(clazz)).map(BeanContainer::getValue).collect(Collectors.toSet());
    }

    @Override
    public <T> Collection<T> getBeans(final String name)
    {
        //noinspection unchecked
        return (Collection<T>) this.beanStream().filter(bean -> bean.getName().equals(name)).map(BeanContainer::getValue).collect(Collectors.toSet());
    }
}
