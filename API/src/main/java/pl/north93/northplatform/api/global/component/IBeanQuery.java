package pl.north93.northplatform.api.global.component;

public interface IBeanQuery
{
    IBeanQuery name(String name);

    IBeanQuery type(Class<?> clazz);

    IBeanQuery requireExactTypeMatch();
}
