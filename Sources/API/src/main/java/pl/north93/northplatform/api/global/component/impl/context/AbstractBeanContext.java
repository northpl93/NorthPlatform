package pl.north93.northplatform.api.global.component.impl.context;

import static java.util.Collections.synchronizedSet;


import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.impl.general.BeanQuery;
import pl.north93.northplatform.api.global.component.IBeanContext;
import pl.north93.northplatform.api.global.component.IBeanQuery;
import pl.north93.northplatform.api.global.component.exceptions.BeanCollisionException;
import pl.north93.northplatform.api.global.component.exceptions.BeanNotFoundException;
import pl.north93.northplatform.api.global.component.impl.container.AbstractBeanContainer;

public abstract class AbstractBeanContext implements IBeanContext
{
    protected final AbstractBeanContext        parent;
    protected final String                     name;
    protected final Set<AbstractBeanContainer> registeredBeans;

    public AbstractBeanContext(final AbstractBeanContext parent, final String name)
    {
        this.parent = parent;
        this.registeredBeans = synchronizedSet(new HashSet<>()); // ensure safety in multithreaded environment
        this.name = name;
    }

    public void add(final AbstractBeanContainer bean)
    {
        final Object result = this.getBeanContainer0(new BeanQuery().type(bean.getType()).name(bean.getName()));
        if (result != null)
        {
            throw new BeanCollisionException(bean);
        }
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

    @Override
    public <T> T getBean(final IBeanQuery query)
    {
        //noinspection unchecked
        return (T) this.getBeanContainer(query).getValue(null);
    }

    @Override
    public boolean isBeanExists(final IBeanQuery query)
    {
        return this.getBeanContainer0(query) != null;
    }

    public final AbstractBeanContainer getBeanContainer(final IBeanQuery query)
    {
        final AbstractBeanContainer result = this.getBeanContainer0(query);
        if (result == null)
        {
            throw new BeanNotFoundException(query);
        }
        return result;
    }

    protected AbstractBeanContainer getBeanContainer0(final IBeanQuery query)
    {
        final BeanQuery queryImpl = (BeanQuery) query;
        for (final AbstractBeanContainer registeredBean : this.registeredBeans)
        {
            if (! queryImpl.test(registeredBean))
            {
                continue;
            }

            return registeredBean;
        }
        if (this.parent == null)
        {
            return null;
        }
        return this.parent.getBeanContainer0(query);
    }

    @Override
    public <T> T getBean(final Class<T> clazz)
    {
        return this.getBean(new BeanQuery().type(clazz));
    }

    @Override
    public boolean isBeanExists(final Class<?> clazz)
    {
        return this.isBeanExists(new BeanQuery().type(clazz));
    }

    @Override
    public <T> T getBean(final String beanName)
    {
        return this.getBean(new BeanQuery().name(beanName));
    }

    @Override
    public boolean isBeanExists(final String beanName)
    {
        return this.isBeanExists(new BeanQuery().name(beanName));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("parent", this.parent).append("name", this.name).append("registeredBeans", this.registeredBeans).toString();
    }
}
