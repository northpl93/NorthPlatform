package pl.north93.zgame.controller.configserver.source;

public interface IConfigSource<T>
{
    Class<T> getType();

    T load();
}
