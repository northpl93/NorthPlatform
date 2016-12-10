package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Field;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

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
