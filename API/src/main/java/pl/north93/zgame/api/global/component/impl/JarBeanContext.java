package pl.north93.zgame.api.global.component.impl;

import java.util.Collection;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class JarBeanContext extends AbstractBeanContext
{
    private final JarComponentLoader loader;

    public JarBeanContext(final RootBeanContext root, final JarComponentLoader loader)
    {
        super(root, "jar-" + loader.getFileUrl().getPath());
        this.loader = loader;
    }

    @Override
    public Collection<AbstractBeanContainer> getAll(final boolean withParent)
    {
        final Collection<AbstractBeanContainer> all = super.getAll(withParent);
        for (final JarComponentLoader jarComponentLoader : this.loader.getDependencies())
        {
            all.addAll(jarComponentLoader.getBeanContext().getAll(withParent));
        }
        return all;
    }

    @Override
    protected Stream<AbstractBeanContainer> beanStream(final boolean withParent)
    {
        Stream<AbstractBeanContainer> stream = super.beanStream(withParent);
        for (final JarComponentLoader jarComponentLoader : this.loader.getDependencies())
        {
            stream = Stream.concat(stream, jarComponentLoader.getBeanContext().beanStream(withParent));
        }
        return stream;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("loader", this.loader).toString();
    }
}
