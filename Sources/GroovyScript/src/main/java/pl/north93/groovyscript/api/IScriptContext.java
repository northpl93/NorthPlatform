package pl.north93.groovyscript.api;

import java.util.Collection;

public interface IScriptContext
{
    Collection<IScriptResource<?>> getResources();

    void addResource(IScriptResource<?> resource);

    boolean isDestroyed();

    void destroy();
}
