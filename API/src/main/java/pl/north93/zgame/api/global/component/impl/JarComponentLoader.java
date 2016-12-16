package pl.north93.zgame.api.global.component.impl;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.IComponentManager;

class JarComponentLoader extends URLClassLoader
{
    private final URL          fileUrl;
    private final List<String> scannedPackages;

    public JarComponentLoader(final URL url, final ClassLoader parent)
    {
        super(new URL[] { url }, parent);
        this.fileUrl = url;
        this.scannedPackages = new ArrayList<>(4);
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

    public void scan(final IComponentManager componentManager, final String packageToScan)
    {
        if (this.scannedPackages.contains(packageToScan))
        {
            return;
        }

        ClassScanner.scan(this.fileUrl, this, componentManager, packageToScan);
        this.scannedPackages.add(packageToScan);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fileUrl", this.fileUrl).toString();
    }
}
