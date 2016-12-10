package pl.north93.zgame.api.global.component.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.IExtensionPoint;

class JarComponentLoader extends URLClassLoader
{
    private final URL fileUrl;
    private boolean   isComponentsScanned;

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

    public void scan(final IComponentManager componentManager)
    {
        if (this.isComponentsScanned)
        {
            return;
        }

        final ClassPool classPool = new ClassPool();
        classPool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader())); // main API loader
        classPool.appendClassPath(new LoaderClassPath(this)); // this Component loader

        final ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.setClassLoaders(new ClassLoader[]{this});
        configuration.setScanners(new SubTypesScanner(false)); // defaultly will exclude Object.class
        configuration.setUrls(this.fileUrl);
        final Reflections reflections = new Reflections(configuration);

        for (final Class<?> aClass : reflections.getSubTypesOf(Object.class))
        {
            try
            {
                final CtClass ctClass = classPool.get(aClass.getName());
                for (final CtConstructor ctConstructor : ctClass.getConstructors())
                {
                    ctConstructor.insertAfter(Injector.class.getName() + ".inject(" + API.class.getName() + ".getApiCore().getComponentManager(), this);");
                }
                API.getApiCore().getInstrumentationClient().redefineClass(aClass.getName(), ctClass.toBytecode());
            }
            catch (final NotFoundException | CannotCompileException | IOException e)
            {
                e.printStackTrace();
            }
        }

        for (final IComponentBundle iComponentBundle : componentManager.getComponents())
        {
            for (final IExtensionPoint<?> iExtensionPoint : iComponentBundle.getExtensionPoints())
            {
                final Class<?> clazzToSearch = iExtensionPoint.getExtensionPointClass();

                @SuppressWarnings("unchecked")
                final Set<Class<?>> extensions = (Set<Class<?>>) reflections.getSubTypesOf(clazzToSearch);
                for (final Class<?> extension : extensions)
                {
                    try
                    {
                        final Object instanceOfExtension = extension.getConstructor().newInstance();
                        Injector.inject(componentManager, instanceOfExtension); // inject fields
                        iExtensionPoint.addImplementation(instanceOfExtension);
                    }
                    catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.isComponentsScanned = true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("fileUrl", this.fileUrl).toString();
    }
}
