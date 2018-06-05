package pl.north93.groovyscript.api;

import org.codehaus.groovy.reflection.ReflectionUtils;

import groovy.lang.GroovyClassLoader;
import pl.north93.groovyscript.api.source.IScriptSource;

public interface IGroovyManager
{
    /**
     * Tworzy nowy kontekst skryptów dla podanych źródeł.
     *
     * @param scriptSource Źródła z których zostanie utworzony kontekst.
     * @return Nowy kontekst.
     */
    IScriptContext createContext(IScriptSource scriptSource);

    /**
     * Zwraca {@link IScriptContext} dla metody wywołującej metodę w której
     * znajduje się wywołanie tej metody.
     *
     * @return Kontekst według powyższego opisu.
     */
    default IScriptContext getCallerContext()
    {
        final Class caller = ReflectionUtils.getCallingClass(2);

        final ClassLoader loader = caller.getClassLoader();
        if (loader instanceof GroovyClassLoader.InnerLoader)
        {
            return this.getContextByClassLoader(loader.getParent());
        }

        return this.getContextByClassLoader(loader);
    }

    /**
     * Zwraca kontekst dla podanego {@link ClassLoader}.
     *
     * @param classLoader ClassLoader dla którego sprawdzamy kontekst.
     * @return IScriptContext powiązany z tym ClassLoaderem.
     */
    IScriptContext getContextByClassLoader(ClassLoader classLoader);
}
