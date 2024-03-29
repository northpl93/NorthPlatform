package pl.north93.northplatform.api.global.component.impl.aggregation;

import java.lang.reflect.Method;

import org.diorite.commons.lazy.LazyValue;

import javassist.CtClass;
import pl.north93.northplatform.api.global.component.impl.context.AbstractBeanContext;

interface IAggregator
{
    boolean isSuitableFor(CtClass clazz);

    void call(AbstractBeanContext beanContext, CtClass clazz, Class<?> javaClass, LazyValue<Object> instance, Method listener);
}
