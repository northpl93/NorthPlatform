package pl.north93.zgame.api.global.component.impl.context;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.impl.JarComponentLoader;

public class JarBeanContext extends AbstractBeanContext
{
    private final JarComponentLoader loader;

    public JarBeanContext(final RootBeanContext root, final JarComponentLoader loader)
    {
        super(root, "jar-" + loader.getFileUrl().getPath());
        this.loader = loader;
    }

    /*@Override
    public Collection<AbstractBeanContainer> getAll(final boolean withParent)
    {
        final Collection<AbstractBeanContainer> all = super.getAll(withParent);
        for (final JarComponentLoader jarComponentLoader : this.loader.getDependencies())
        {
            all.addAll(jarComponentLoader.getBeanContext().getAll(withParent));
        }
        return all;
    }*/

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("loader", this.loader).toString();
    }
}
