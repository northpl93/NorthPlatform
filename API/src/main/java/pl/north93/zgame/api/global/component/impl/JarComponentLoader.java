package pl.north93.zgame.api.global.component.impl;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class JarComponentLoader extends URLClassLoader
{
    private final URL fileUrl;

    public JarComponentLoader(final URL url, final ClassLoader parent)
    {
        super(new URL[] { url }, parent);
        this.fileUrl = url;
    }

    @Override
    public URL getResource(final String name) // modified to search resources only in this jar
    {
        return this.findResource(name);
    }

    public URL getFileUrl()
    {
        return this.fileUrl;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fileUrl", this.fileUrl).toString();
    }
}
