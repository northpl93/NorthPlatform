package pl.north93.zgame.api.global.component.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.IExtensionPoint;
import pl.north93.zgame.api.global.component.annotations.SkipInjections;

class ClassScanner
{
    static void scan(final URL fileUrl, final ClassLoader classLoader, final IComponentManager componentManager, final String packageToScan)
    {
        final ClassPool classPool = new ClassPool();
        classPool.appendClassPath(new LoaderClassPath(ApiCore.class.getClassLoader())); // main API loader
        classPool.appendClassPath(new LoaderClassPath(classLoader)); // this Component loader

        final ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.setClassLoaders(new ClassLoader[]{classLoader});
        configuration.setScanners(new SubTypesScanner(false)); // defaultly will exclude Object.class
        configuration.setUrls(fileUrl);
        configuration.setInputsFilter(new FilterBuilder().includePackage(packageToScan));
        final Reflections reflections = new Reflections(configuration);

        for (final Class<?> aClass : reflections.getSubTypesOf(Object.class))
        {
            if (aClass.isAnnotationPresent(SkipInjections.class))
            {
                continue;
            }

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
    }
}
