package pl.north93.northplatform.api.global.component.impl.scanner;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import javassist.CtField;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.component.impl.general.BeanQuery;
import pl.north93.northplatform.api.global.component.impl.container.AbstractBeanContainer;
import pl.north93.northplatform.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.northplatform.api.global.component.impl.general.CtUtils;
import pl.north93.northplatform.api.global.component.impl.injection.FieldInjectionContext;

class StaticScanningTask extends AbstractScanningTask
{
    private final Set<CtField> staticFields;

    public StaticScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
        this.staticFields = Arrays.stream(ctClass.getDeclaredFields()).filter(method -> Modifier.isStatic(method.getModifiers())).collect(Collectors.toSet());
    }

    @Override
    boolean tryComplete0()
    {
        final Iterator<CtField> iterator = this.staticFields.iterator();
        while (iterator.hasNext())
        {
            final CtField next = iterator.next();
            if (! next.hasAnnotation(Inject.class))
            {
                iterator.remove();
                continue;
            }

            final Field field;
            
            try
            {
                field = CtUtils.toJavaField(this.clazz, next);
            }
            catch ( Throwable e )
            {
                System.err.println("Dependency inject for class " + clazz.getName() + " fails, because the class cannot be loaded");
                e.printStackTrace();
                return true;
            }
            
            field.setAccessible(true);

            final BeanQuery query = new BeanQuery().type(field.getType());
            final Named namedAnn = field.getAnnotation(Named.class);
            if (namedAnn != null)
            {
                query.name(namedAnn.value());
            }

            try
            {
                final AbstractBeanContainer beanContainer = this.beanContext.getBeanContainer(query);
                field.set(null, beanContainer.getValue(new FieldInjectionContext(null, field)));
                iterator.remove();
            }
            catch (final Exception exception)
            {
                this.lastCause = exception;
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
