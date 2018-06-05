package pl.north93.groovyscript.api.source;

import groovy.lang.GroovyClassLoader;

public interface IScriptSource
{
    void setup(GroovyClassLoader loader);
}
