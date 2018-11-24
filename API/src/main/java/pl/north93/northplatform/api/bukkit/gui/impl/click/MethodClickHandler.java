package pl.north93.northplatform.api.bukkit.gui.impl.click;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.gui.event.ClickEvent;
import pl.north93.northplatform.api.global.utils.lang.SneakyThrow;

public class MethodClickHandler implements IClickHandler
{
    private static final Lookup     LOOKUP = MethodHandles.lookup();
    private static final MethodType TYPE   = MethodType.methodType(void.class, IClickSource.class, ClickEvent.class);
    private final MethodHandle method;

    public MethodClickHandler(final Method method)
    {
        try
        {
            this.method = LOOKUP.unreflect(method).asType(TYPE);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(final IClickSource source, final ClickEvent event)
    {
        try
        {
            this.method.invokeExact(source, event);
        }
        catch (final Throwable throwable)
        {
            if (throwable instanceof Exception)
            {
                throw new RuntimeException("Exception thrown while executing gui click handler", throwable);
            }

            // nie próbujemy zlapac Throwable typu OutOfMemoryError
            SneakyThrow.sneaky(throwable);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("method", this.method).toString();
    }
}
