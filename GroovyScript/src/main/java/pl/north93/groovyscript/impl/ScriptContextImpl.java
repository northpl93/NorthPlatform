package pl.north93.groovyscript.impl;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.sets.ConcurrentSet;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovySystem;
import pl.north93.groovyscript.api.IScriptContext;
import pl.north93.groovyscript.api.IScriptResource;
import pl.north93.northplatform.api.global.utils.lang.JavaUtils;

/*default*/ class ScriptContextImpl implements IScriptContext
{
    private final GroovyManagerImpl           groovyManager;
    private final Collection<IScriptResource> resources;
    private final GroovyClassLoader           classLoader;
    private boolean destroyed;

    public ScriptContextImpl(final GroovyManagerImpl groovyManager, final GroovyClassLoader classLoader)
    {
        this.groovyManager = groovyManager;
        this.classLoader = classLoader;
        this.resources = new ConcurrentSet<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<IScriptResource<?>> getResources()
    {
        this.checkDestroyed();
        return Collections.unmodifiableCollection((Collection) this.resources);
    }

    @Override
    public void addResource(final IScriptResource<?> resource)
    {
        Preconditions.checkState(! resource.isDestroyed(), "Resource is destroyed!");
        this.checkDestroyed();

        this.resources.add(resource);
    }

    @Override
    public boolean isDestroyed()
    {
        return this.destroyed;
    }

    public GroovyClassLoader getClassLoader()
    {
        return this.classLoader;
    }

    @Override
    public void destroy()
    {
        this.destroyed = true;
        this.groovyManager.removeDestroyedContext(this);

        for (final IScriptResource resource : this.resources)
        {
            if (resource.isDestroyed())
            {
                continue;
            }

            resource.destroy();
        }
        this.resources.clear();

        for (final Class clazz : this.classLoader.getLoadedClasses())
        {
            GroovySystem.getMetaClassRegistry().removeMetaClass(clazz);
        }

        JavaUtils.hideException(this.classLoader::close);
    }

    private void checkDestroyed()
    {
        if (this.destroyed)
        {
            throw new IllegalStateException("Script context is destroyed");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("resources", this.resources).append("classLoader", this.classLoader).append("destroyed", this.destroyed).toString();
    }
}
