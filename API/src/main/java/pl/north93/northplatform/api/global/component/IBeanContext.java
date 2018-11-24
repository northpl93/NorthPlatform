package pl.north93.northplatform.api.global.component;

public interface IBeanContext
{
    IBeanContext getParent();

    String getBeanContextName();

    <T> T getBean(IBeanQuery query);

    boolean isBeanExists(IBeanQuery query);

    <T> T getBean(Class<T> clazz);

    boolean isBeanExists(Class<?> clazz);

    <T> T getBean(String beanName);

    boolean isBeanExists(String beanName);
}
