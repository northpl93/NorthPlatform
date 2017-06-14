package pl.north93.zgame.api.global.component.impl.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    public Collection<AbstractBeanContainer> getAll(final boolean withParent)
    {
        final Collection<AbstractBeanContainer> all = super.getAll(withParent);
        for (final ComponentBeanContext dependency : this.dependencies)
        {
            all.addAll(dependency.getAll(false));
        }
        return all;
    }
}
