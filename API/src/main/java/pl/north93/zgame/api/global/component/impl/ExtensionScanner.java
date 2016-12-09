package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.IExtensionPoint;

class ExtensionScanner
{
    public static void scan(final IComponentManager componentManager, final JarComponentLoader jarComponentLoader)
    {
        for (final IComponentBundle iComponentBundle : componentManager.getComponents())
        {
            for (final IExtensionPoint<?> iExtensionPoint : iComponentBundle.getExtensionPoints())
            {
                final Class<?> clazzToSearch = iExtensionPoint.getExtensionPointClass();

                final ConfigurationBuilder configuration = new ConfigurationBuilder();
                configuration.setClassLoaders(new ClassLoader[]{jarComponentLoader});
                configuration.setScanners(new SubTypesScanner()); // defaultly will exclude Object.class
                configuration.setUrls(jarComponentLoader.getFileUrl());

                final Reflections reflections = new Reflections(configuration);

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
                        continue;
                    }
                }
            }
        }
    }
}
