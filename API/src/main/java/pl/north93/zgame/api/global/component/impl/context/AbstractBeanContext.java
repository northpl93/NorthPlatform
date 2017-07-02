package pl.north93.zgame.api.global.component.impl.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.IBeanContext;
import pl.north93.zgame.api.global.component.IBeanQuery;
import pl.north93.zgame.api.global.component.exceptions.BeanNotFoundException;
import pl.north93.zgame.api.global.component.impl.BeanQuery;
import pl.north93.zgame.api.global.component.impl.container.AbstractBeanContainer;

public abstract class AbstractBeanContext implements IBeanContext
{
    protected final AbstractBeanContext        parent;
    protected final String                     name;
    protected final Set<AbstractBeanContainer> registeredBeans;

    public AbstractBeanContext(final AbstractBeanContext parent, final String name)
    {
        this.parent = parent;
        this.registeredBeans = new HashSet<>();
        this.name = name;
    }

    public void add(final AbstractBeanContainer bean)
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

    public Collection<AbstractBeanContainer> getAll(final boolean withParent)
    {
        if (withParent && this.parent != null)
        {
            final Set<AbstractBeanContainer> beans = new HashSet<>();
            beans.addAll(this.parent.getAll(true));
            beans.addAll(this.registeredBeans);
            return beans;
        }
        return this.registeredBeans;
    }

    @Override
    public <T> T getBean(final IBeanQuery query)
    {
        //noinspection unchecked
        return (T) this.beanStream(true)
                       .filter((BeanQuery) query)
                       .reduce((u, v) -> { throw new IllegalStateException("More than one bean found. Use getBeans!"); })
                       .map(container -> container.getValue(null))
                       .orElseThrow(() -> new BeanNotFoundException(query));
    }

    public AbstractBeanContainer getBeanContainer(final IBeanQuery query)
    {
        //noinspection unchecked
        return this.beanStream(true)
                   .filter((BeanQuery) query)
                   .reduce((u, v) -> { throw new IllegalStateException("More than one bean found. Use getBeans!"); })
                   .orElseThrow(() -> new BeanNotFoundException(query));
    }

    @Override
    public <T> Collection<T> getBeans(final IBeanQuery query)
    {
        //noinspection unchecked
        return (Collection<T>) this.beanStream(true)
                                   .filter((BeanQuery) query)
                                   .map(container -> container.getValue(null))
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

    protected Stream<AbstractBeanContainer> beanStream(final boolean withParent)
    {
        return this.getAll(withParent).stream();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("parent", this.parent).append("name", this.name).append("registeredBeans", this.registeredBeans).toString();
    }
}