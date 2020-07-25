package pl.north93.northplatform.api.global.component.impl.aggregation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.diorite.commons.lazy.LazyValue;

import javassist.CtClass;
import javassist.NotFoundException;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import pl.north93.northplatform.api.global.component.impl.context.AbstractBeanContext;
import pl.north93.northplatform.api.global.component.impl.context.TemporaryBeanContext;
import pl.north93.northplatform.api.global.component.impl.general.SmartExecutor;

@ToString
@EqualsAndHashCode
class InstancesAggregator implements IAggregator
{
    private final String className;
    private final boolean isInterface;

    public InstancesAggregator(final CtClass clazz)
    {
        this.className = clazz.getName();
        this.isInterface = clazz.isInterface();
    }

    @Override
    public boolean isSuitableFor(final CtClass clazz)
    {
        return ! Modifier.isAbstract(clazz.getModifiers()) && ! clazz.isInterface() && this.isSuitable(clazz);
    }

    @SneakyThrows(NotFoundException.class)
    private boolean isSuitable(final CtClass checkedClass)
    {
        if (this.isInterface)
        {
            final CtClass[] interfaces = checkedClass.getInterfaces();
            for (final CtClass anInterface : interfaces)
            {
                if (anInterface.getName().equals(this.className))
                {
                    return true;
                }
            }
        }

        final CtClass superclass = checkedClass.getSuperclass();
        if (superclass != null && !superclass.getName().equals("java.lang.Object"))
        {
            return superclass.getName().equals(this.className) || this.isSuitable(superclass);
        }

        // nic nie pasuje
        return false;
    }

    @Override
    public void call(final AbstractBeanContext beanContext, final CtClass ctClass, final Class<?> javaClass, final LazyValue<Object> instance, final Method listener)
    {
        final TemporaryBeanContext tempContext = new TemporaryBeanContext(beanContext);
        tempContext.putIfAbsent(javaClass, instance.get()); // instancja moze byc zarejestrowanym beanem

        if (Modifier.isStatic(listener.getModifiers()))
        {
            SmartExecutor.execute(listener, tempContext, null);
        }
        else
        {
            SmartExecutor.execute(listener, tempContext, beanContext.getBean(listener.getDeclaringClass()));
        }
    }
}
