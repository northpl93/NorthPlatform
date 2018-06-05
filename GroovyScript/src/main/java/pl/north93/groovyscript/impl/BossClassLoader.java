package pl.north93.groovyscript.impl;

import java.net.URL;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.impl.general.JarComponentLoader;
import pl.north93.zgame.api.global.utils.lang.JavaUtils;

/*default*/ class BossClassLoader extends ClassLoader
{
    @Inject
    private ApiCore apiCore;

    @Override
    public URL getResource(final String name)
    {
        for (final IComponentBundle bundle : this.apiCore.getComponentManager().getComponents())
        {
            final JarComponentLoader loader = JavaUtils.instanceOf(bundle.getClassLoader(), JarComponentLoader.class);
            if (loader == null)
            {
                continue;
            }

            final URL resource = loader.getResource(name);
            if (resource == null)
            {
                continue;
            }

            return resource;
        }

        return super.getResource(name);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException
    {
        for (final IComponentBundle bundle : this.apiCore.getComponentManager().getComponents())
        {
            final JarComponentLoader loader = JavaUtils.instanceOf(bundle.getClassLoader(), JarComponentLoader.class);
            if (loader == null)
            {
                continue;
            }

            try
            {
                return Class.forName(name, false, loader);
            }
            catch (final ClassNotFoundException ignored)
            {
            }
        }

        return super.findClass(name);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
