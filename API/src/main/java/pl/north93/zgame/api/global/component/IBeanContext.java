package pl.north93.zgame.api.global.component;

public interface IBeanContext
{
    IBeanContext getParent();

    String getBeanContextName();

    <T> T getBean(IBeanQuery query);

    <T> T getBean(Class<T> clazz);

    <T> T getBean(String beanName);
}
