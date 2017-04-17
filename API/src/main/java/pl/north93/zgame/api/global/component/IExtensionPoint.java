package pl.north93.zgame.api.global.component;

import java.util.List;

import org.reflections.Reflections;

public interface IExtensionPoint<T>
{
    Class<T> getExtensionPointClass();

    List<T> getImplementations();

    void addImplementation(Object impl);

    void setHandler(IExtensionHandler<T> handler);

    void scan(Reflections reflections);
}
