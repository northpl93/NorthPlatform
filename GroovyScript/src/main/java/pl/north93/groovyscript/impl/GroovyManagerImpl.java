package pl.north93.groovyscript.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.GroovyClassLoader;
import pl.north93.groovyscript.api.IGroovyManager;
import pl.north93.groovyscript.api.IScriptContext;
import pl.north93.groovyscript.api.source.IScriptSource;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/*default*/ class GroovyManagerImpl implements IGroovyManager
{
    @Inject
    private Logger logger;
    private final BossClassLoader bossClassLoader;
    private final Map<ClassLoader, IScriptContext> context;

    @Bean
    private GroovyManagerImpl()
    {
        this.context = new HashMap<>();
        this.bossClassLoader = new BossClassLoader();
    }

    @Override
    public IScriptContext createContext(final IScriptSource scriptSource)
    {
        final CompilerConfiguration config = new CompilerConfiguration();
        config.setTargetBytecode(CompilerConfiguration.JDK8);
        config.getOptimizationOptions().put("indy", true); // enable invokedynamic

        final GroovyClassLoader loader = new GroovyClassLoader(this.bossClassLoader);

        final ScriptContextImpl context = new ScriptContextImpl(this, loader);
        this.context.put(loader, context);

        scriptSource.setup(loader);

        this.logger.log(Level.INFO, "Created new groovy ScriptContext");
        return context;
    }

    @Override
    public IScriptContext getContextByClassLoader(final ClassLoader classLoader)
    {
        return this.context.get(classLoader);
    }

    public void removeDestroyedContext(final ScriptContextImpl context)
    {
        Preconditions.checkState(context.isDestroyed());
        this.context.remove(context.getClassLoader());

        this.logger.log(Level.INFO, "Groovy script context removed");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("context", this.context).toString();
    }
}