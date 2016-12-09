package pl.north93.zgame.api.global.component;

import java.util.List;

public interface IExtensionPoint<T>
{
    Class<T> getExtensionPointClass();

    List<T> getImplementations();

    void addImplementation(Object impl);

    void setHandler(IExtensionHandler<T> handler);
}
