package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.utils.UTF8Control;

public class Injector
{
    public static void inject(final IComponentManager componentManager, final Object instance)
    {
        final Class<?> clazz = instance.getClass();
        for (final Field field : clazz.getDeclaredFields())
        {
            field.setAccessible(true);
            if (field.isAnnotationPresent(InjectComponent.class))
            {
                final InjectComponent annotation = field.getAnnotation(InjectComponent.class);
                final Component component = componentManager.getComponent(annotation.value());

                try
                {
                    field.set(instance, component);
                }
                catch (final IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                continue;
            }

            if (field.isAnnotationPresent(InjectResource.class))
            {
                final InjectResource annotation = field.getAnnotation(InjectResource.class);
                final ClassLoader classLoader = instance.getClass().getClassLoader();
                final ResourceBundle bundle;
                if (StringUtils.isEmpty(annotation.locale()))
                {
                    bundle = ResourceBundle.getBundle(annotation.bundleName(), Locale.getDefault(), classLoader, new UTF8Control());
                }
                else
                {
                    bundle = ResourceBundle.getBundle(annotation.bundleName(), Locale.forLanguageTag(annotation.locale()), classLoader, new UTF8Control());
                }

                try
                {
                    field.set(instance, bundle);
                }
                catch (final IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }

            if (field.getType().isAssignableFrom(ApiCore.class))
            {
                try
                {
                    System.out.println("ApiCore set on " + instance);
                    field.set(instance, API.getApiCore());
                }
                catch (final IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
