package pl.north93.northplatform.api.bukkit.gui.impl;

import static org.diorite.commons.reflections.DioriteReflectionUtils.getMethod;


import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.MethodInvoker;

import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickHandler;
import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickSource;
import pl.north93.northplatform.api.bukkit.gui.impl.click.MethodClickHandler;
import pl.north93.northplatform.api.bukkit.gui.impl.click.NorthUriClickHandler;
import pl.north93.northplatform.api.bukkit.gui.ClickHandler;

public class ClickHandlerManager
{
    private static ClickHandlerManager              instance;
    private final Map<ClickCacheKey, IClickHandler> cachedHandlers = new WeakHashMap<>();

    public static ClickHandlerManager getInstance()
    {
        if (instance == null)
        {
            instance = new ClickHandlerManager();
        }

        return instance;
    }

    public IClickHandler processClickHandler(final IClickSource gui, final String clickString)
    {
        if (clickString.startsWith("northplatform:"))
        {
            return new NorthUriClickHandler(clickString);
        }
        else
        {
            final Class<? extends IClickSource> guiClass = gui.getClass();
            return this.cachedHandlers.computeIfAbsent(new ClickCacheKey(guiClass, clickString), this::computeKey);
        }
    }

    private IClickHandler computeKey(final ClickCacheKey key)
    {
        final MethodInvoker invoker = getMethod(key.getClazz(), key.getClick(), false);
        if (invoker == null)
        {
            return null;
        }

        final Method method = invoker.getMethod();
        if (! method.isAnnotationPresent(ClickHandler.class))
        {
            return null;
        }

        return new MethodClickHandler(method);
    }
}

final class ClickCacheKey
{
    private final Class<?> clazz;
    private final String   click;

    public ClickCacheKey(final Class<?> clazz, final String click)
    {
        this.clazz = clazz;
        this.click = click;
    }

    public Class<?> getClazz()
    {
        return this.clazz;
    }

    public String getClick()
    {
        return this.click;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }
        final ClickCacheKey that = (ClickCacheKey) o;
        return Objects.equals(this.clazz, that.clazz) && Objects.equals(this.click, that.click);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.clazz, this.click);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clazz", this.clazz).append("click", this.click).toString();
    }
}