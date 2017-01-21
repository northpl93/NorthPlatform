package pl.north93.zgame.api.global.component.impl;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import pl.north93.zgame.api.global.component.IComponentManager;

class JarComponentLoader extends URLClassLoader
{
    private final URL         fileUrl;
    private final Set<String> scannedPackages;

    public JarComponentLoader(final URL url, final ClassLoader parent)
    {
        super(new URL[] { url }, parent);
        this.fileUrl = url;
        this.scannedPackages = new ObjectArraySet<>(4);
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

    public void scan(final IComponentManager componentManager, final Set<String> packagesToScan)
    {
        final Sets.SetView<String> toScan = Sets.difference(packagesToScan, this.scannedPackages);

        if (toScan.isEmpty())
        {
            return;
        }

        ClassScanner.scan(this.fileUrl, this, componentManager, toScan);
        this.scannedPackages.addAll(toScan);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fileUrl", this.fileUrl).toString();
    }
}
