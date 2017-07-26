package pl.north93.zgame.api.global.component.impl.context;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.IBeanQuery;
import pl.north93.zgame.api.global.component.impl.container.AbstractBeanContainer;

public class ComponentBeanContext extends AbstractBeanContext
{
    private final Set<ComponentBeanContext> dependencies = new HashSet<>();

    public ComponentBeanContext(final JarBeanContext parent, final String componentName)
    {
        super(parent, "component-" + componentName);
    }

    public void addDependency(final ComponentBeanContext componentBeanContext)
    {
        if (this == componentBeanContext)
        {
            return;
        }
        this.dependencies.add(componentBeanContext);
    }

    @Override
    protected AbstractBeanContainer getBeanContainer0(final IBeanQuery query)
    {
        AbstractBeanContainer result = super.getBeanContainer0(query);
        if (result == null)
        {
            for (final ComponentBeanContext dependency : this.dependencies)
            {
                result = dependency.getBeanContainer0(query);
                if (result != null)
                {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("dependencies", this.dependencies).toString();
    }
}
