package pl.north93.zgame.api.global.component.impl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

class DynamicBeanContainer extends AbstractBeanContainer
{
    private final Method method;

    public DynamicBeanContainer(final Class<?> type, final String name, final Method method)
    {
        super(type, name);
        this.method = method;
    }

    @Override
    public Object getValue(final AccessibleObject injectionContext)
    {
        injectionContext.getAnnotations()
        return null;
    }
}
