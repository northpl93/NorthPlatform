package pl.north93.northplatform.api.global.component.impl.scanner;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javassist.CtClass;
import javassist.CtConstructor;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.impl.container.BeanFactory;
import pl.north93.northplatform.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.northplatform.api.global.component.impl.general.CtUtils;

class ConstructorScanningTask extends AbstractScanningTask
{
    private final Set<CtConstructor> constructors;

    public ConstructorScanningTask(final ClassloaderScanningTask classloaderScanner, final Class<?> clazz, final CtClass ctClass, final AbstractBeanContext beanContext)
    {
        super(classloaderScanner, clazz, ctClass, beanContext);
        this.constructors = new HashSet<>(Arrays.asList(this.ctClass.getDeclaredConstructors()));
    }

    @Override
    boolean tryComplete0()
    {
        final Iterator<CtConstructor> iterator = this.constructors.iterator();
        while (iterator.hasNext())
        {
            final CtConstructor constructor = iterator.next();
            try
            {
                if (this.isConstructorBean(constructor))
                {
                    final Constructor<?> javaConstructor = CtUtils.toJavaConstructor(this.clazz, constructor);
                    BeanFactory.INSTANCE.createStaticBean(this.beanContext, javaConstructor);
                }
            }
            catch (final Exception exception)
            {
                this.lastCause = exception;
                continue;
            }
            iterator.remove();
        }
        return this.constructors.isEmpty();
    }

    // sprawdza czy dany konstruktor jest beanem i czy jest aktywny
    private boolean isConstructorBean(final CtConstructor constructor) throws ClassNotFoundException
    {
        return constructor.hasAnnotation(Bean.class) && this.classloaderScanner.getManager().getProfileManager().isActive(this.clazz.getClassLoader(), constructor);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("constructors", this.constructors).toString();
    }
}
