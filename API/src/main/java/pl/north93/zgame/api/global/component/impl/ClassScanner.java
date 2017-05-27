package pl.north93.zgame.api.global.component.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
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
import pl.north93.zgame.api.global.component.annotations.*;

class ClassScanner
{
    private static final String INJECTOR_NAME = Injector.class.getName();
    private static final String API_NAME = API.class.getName();

    static void scan(final URL fileUrl, final ClassLoader classLoader, final IComponentManager componentManager)
    {
        final ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.setClassLoaders(new ClassLoader[]{classLoader});
        configuration.setScanners(new SubTypesScanner(false), new MethodAnnotationsScanner(), new FieldAnnotationsScanner());
        configuration.setUrls(fileUrl);

        final Reflections reflections = new Reflections(configuration);
        final ClassPool classPool = getClassPool(classLoader);

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
                    ctConstructor.insertAfter(INJECTOR_NAME + ".inject(" + API_NAME + ".getApiCore().getComponentManager(), this);");
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
                iExtensionPoint.scan(reflections);
            }
        }
    }

    private static ClassPool getClassPool(final ClassLoader classLoader)
    {
        if (classLoader instanceof JarComponentLoader)
        {
            final JarComponentLoader componentLoader = (JarComponentLoader) classLoader;
            return componentLoader.getClassPool();
        }
        else
        {
            final ClassPool classPool = new ClassPool();
            classPool.appendClassPath(new LoaderClassPath(ApiCore.class.getClassLoader()));
            classPool.appendClassPath(new LoaderClassPath(classLoader));
            return classPool;
        }
    }

    private static Set<Class<?>> getAllClasses(final Reflections reflections)
    {
        return Sets.newHashSet(ReflectionUtils.forNames(reflections.getStore().get(SubTypesScanner.class.getSimpleName()).values(), reflections.getConfiguration().getClassLoaders()));
    }

    private static boolean shouldAddInjector(final Class<?> clazz)
    {
        final Field[] declaredFields;
        try
        {
            declaredFields = clazz.getDeclaredFields();
        }
        catch (final NoClassDefFoundError ex)
        {
            // may occur when we're scanning external classloader
            return false;
        }

        if (clazz.isAnnotationPresent(Bean.class) )
        {
            return true;
        }

        for (final Field field : declaredFields)
        {
            if (field.isAnnotationPresent(Inject.class))
            {
                return true;
            }
        }

        return false;
    }
}
