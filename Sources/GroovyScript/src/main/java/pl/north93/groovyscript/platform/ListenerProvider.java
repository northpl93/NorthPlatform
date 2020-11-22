package pl.north93.groovyscript.platform;

import static org.diorite.commons.reflections.DioriteReflectionUtils.getField;


import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.FieldAccessor;

import pl.north93.groovyscript.api.IGroovyManager;
import pl.north93.groovyscript.api.IScriptContext;
import pl.north93.groovyscript.api.IScriptResource;
import pl.north93.northplatform.api.bukkit.BukkitHostConnector;
import pl.north93.northplatform.api.bukkit.Main;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public final class ListenerProvider
{
    @Inject
    private static IGroovyManager groovyManager;
    @Inject
    private static BukkitHostConnector hostConnector;

    public static <T extends Event> void listener(final Class<T> eventClazz, final Consumer<T> action)
    {
        final IScriptContext context = groovyManager.getCallerContext();
        register(context, eventClazz, EventPriority.NORMAL, action);
    }

    public static <T extends Event> void listener(final Class<T> eventClazz, final EventPriority priority, final Consumer<T> action)
    {
        final IScriptContext context = groovyManager.getCallerContext();
        register(context, eventClazz, priority, action);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> void register(final IScriptContext context, final Class<T> eventClazz, final EventPriority priority, final Consumer<T> action)
    {
        final EventExecutor executor = (listener, event) -> action.accept((T) event);
        final Main plugin = hostConnector.getPluginMain();

        final ListenerScriptResource resource = new ListenerScriptResource(eventClazz);
        context.addResource(resource);

        Bukkit.getPluginManager().registerEvent(eventClazz, resource, priority, executor, plugin);
    }
}

class ListenerScriptResource implements IScriptResource<Listener>, Listener
{
    private final Class<? extends Event> eventClass;
    private boolean destroyed;

    public ListenerScriptResource(final Class<? extends Event> eventClass)
    {
        this.eventClass = eventClass;
    }

    @Override
    public Listener get()
    {
        return this;
    }

    @Override
    public boolean isDestroyed()
    {
        return this.destroyed;
    }

    @Override
    public void destroy()
    {
        if (this.destroyed)
        {
            return;
        }

        final FieldAccessor<HandlerList> field = getField(this.eventClass, HandlerList.class, 0);

        final HandlerList handlerList = field.get(null);
        assert handlerList != null;

        handlerList.unregister(this);
        this.destroyed = true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("eventClass", this.eventClass).append("destroyed", this.destroyed).toString();
    }
}