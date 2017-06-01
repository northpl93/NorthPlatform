package pl.north93.zgame.api.global.component;

import java.util.Collection;

public interface IBeanContext
{
    IBeanContext getParent();

    String getBeanContextName();

    <T> T getBean(IBeanQuery query);

    <T> T getBean(Class<T> clazz);

    <T> T getBean(String beanName);

    <T> Collection<T> getBeans(Class<T> clazz);

    <T> Collection<T> getBeans(String name);

    <T> Collection<T> getBeans(IBeanQuery query);
}
