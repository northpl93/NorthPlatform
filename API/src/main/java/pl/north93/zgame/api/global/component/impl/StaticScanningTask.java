package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;

class StaticScanningTask extends AbstractScanningTask
{
    private final Set<Field> staticFields;

    public StaticScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
        Field[] fields;
        try
        {
            fields = clazz.getDeclaredFields();
        }
        catch (final Throwable ignored)
        {
            fields = new Field[0];
        }

        this.staticFields = Arrays.stream(fields).filter(method -> Modifier.isStatic(method.getModifiers())).collect(Collectors.toSet());
    }

    @Override
    boolean tryComplete()
    {
        final Iterator<Field> iterator = this.staticFields.iterator();
        while (iterator.hasNext())
        {
            final Field next = iterator.next();
            if (! next.isAnnotationPresent(Inject.class))
            {
                iterator.remove();
                continue;
            }

            final BeanQuery query = new BeanQuery().type(next.getType());

            final Named namedAnn = next.getAnnotation(Named.class);
            if (namedAnn != null)
            {
                query.name(namedAnn.value());
            }

            next.setAccessible(true);
            try
            {
                next.set(null, this.beanContext.getBean(query));
                iterator.remove();
            }
            catch (final Exception ignored)
            {
            }
        }
        return this.staticFields.isEmpty();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("staticFields", this.staticFields).toString();
    }
}
