package pl.north93.zgame.api.global.component.impl.injection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.component.exceptions.InjectionException;
import pl.north93.zgame.api.global.component.impl.BeanQuery;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;
import pl.north93.zgame.api.global.component.impl.context.AbstractBeanContext;

public class Injector
{
    public static void inject(final Object instance)
    {
        final ComponentManagerImpl manager = ComponentManagerImpl.instance;
        final Class<?> clazz = instance.getClass();

        final AbstractBeanContext context = manager.getOwningContext(clazz);

        for (final Field field : clazz.getDeclaredFields())
        {
            field.setAccessible(true);

            final Inject injectAnn = field.getAnnotation(Inject.class);
            if (injectAnn == null)
            {
                continue;
            }

            if (Modifier.isFinal(field.getModifiers()))
            {
                throw new InjectionException("Tried to inject final field");
            }

            final BeanQuery query = new BeanQuery();
            query.type(field.getType());
            final Named namedAnn = field.getAnnotation(Named.class);
            if (namedAnn != null)
            {
                query.name(namedAnn.value());
            }

            try
            {
                final FieldInjectionContext injectionContext = new FieldInjectionContext(instance, field);
                final Object bean = context.getBeanContainer(query).getValue(injectionContext);
                field.set(instance, bean);
            }
            catch (final Exception e)
            {
                if (injectAnn.silentFail())
                {
                    // kontynuujemy reszte pol
                    continue;
                }
                throw new InjectionException(instance.getClass(), e);
            }
        }
    }
}
