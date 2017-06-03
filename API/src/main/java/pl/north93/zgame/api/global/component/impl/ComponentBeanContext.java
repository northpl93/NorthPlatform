package pl.north93.zgame.api.global.component.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

class ComponentBeanContext extends AbstractBeanContext
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

    @Override
    protected Stream<AbstractBeanContainer> beanStream(final boolean withParent)
    {
        Stream<AbstractBeanContainer> stream = super.beanStream(withParent);
        for (final ComponentBeanContext dependency : this.dependencies)
        {
            stream = Stream.concat(stream, dependency.beanStream(false));
        }
        return stream;
    }
}
