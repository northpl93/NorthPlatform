package pl.north93.groovyscript.api;

public interface IScriptResource<T>
{
    T get();

    boolean isDestroyed();

    void destroy();
}
