package pl.north93.zgame.api.global.component.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.IComponentBundle;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.IExtensionPoint;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.SkipInjections;

class ClassScanner
{
    static void scan(final URL fileUrl, final ClassLoader classLoader, final IComponentManager componentManager, final Set<String> packagesToScan)
    {
        final ClassPool classPool = new ClassPool();
        classPool.appendClassPath(new LoaderClassPath(ApiCore.class.getClassLoader())); // main API loader
        classPool.appendClassPath(new LoaderClassPath(classLoader)); // this Component loader

        final ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.setClassLoaders(new ClassLoader[]{classLoader});
        configuration.setScanners(new SubTypesScanner(false)); // defaultly will exclude Object.class
        configuration.setUrls(fileUrl);

        final FilterBuilder packagesFilter = new FilterBuilder();
        for (final String packageToScan : packagesToScan)
        {
            packagesFilter.includePackage(packageToScan);
        }
        configuration.setInputsFilter(packagesFilter);

        final Reflections reflections = new Reflections(configuration);

        for (final Class<?> aClass : getAllClasses(reflections))
        {
            if (aClass.isAnnotationPresent(SkipInjections.class) || ! shouldAddInjector(aClass))
            {
                continue;
            }

            try
            {
                final CtClass ctClass = classPool.get(aClass.getName());

                final Set<String> postInject = Arrays.stream(ctClass.getDeclaredMethods())
                                                     .filter(ctMethod -> ctMethod.hasAnnotation(PostInject.class))
                                                     .filter(ctMethod -> ! Modifier.isStatic(ctMethod.getModifiers()))
                                                     .map(CtMethod::getName)
                                                     .collect(Collectors.toSet());

                for (final CtConstructor ctConstructor : ctClass.getConstructors())
                {
                    ctConstructor.insertAfter(Injector.class.getName() + ".inject(" + API.class.getName() + ".getApiCore().getComponentManager(), this);");
                    for (final String postInjectMethod : postInject)
                    {
                        ctConstructor.insertAfter("this." + postInjectMethod + "();");
                    }
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
                        // it's not needed(?) because I fixed all class scanning
                        //Injector.inject(componentManager, instanceOfExtension); // inject fields
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

    private static Set<Class<?>> getAllClasses(final Reflections reflections)
    {
        return Sets.newHashSet(ReflectionUtils.forNames(reflections.getStore().get(SubTypesScanner.class.getSimpleName()).values(), reflections.getConfiguration().getClassLoaders()));
    }

    private static boolean shouldAddInjector(final Class<?> clazz)
    {
        for (final Field field : clazz.getDeclaredFields())
        {
            final Class<?> type = field.getType();
            if (field.isAnnotationPresent(InjectComponent.class))
            {
                return true;
            }
            if (field.isAnnotationPresent(InjectResource.class))
            {
                return true;
            }
            if (ApiCore.class.isAssignableFrom(type))
            {
                return true;
            }
            if (Logger.class.equals(type))
            {
                return true;
            }
        }

        return false;
    }
}
