package pl.north93.northplatform.controller.configserver.source;

public interface IConfigSource<T>
{
    Class<T> getType();

    T load();
}
