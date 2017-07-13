package pl.north93.zgame.api.global.uri.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.MessageFormat;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.IBeanContext;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.uri.IUriCallHandler;
import pl.north93.zgame.api.global.uri.IUriManager;
import pl.north93.zgame.api.global.uri.UriHandler;
import pl.north93.zgame.api.global.uri.impl.router.NonorderedRouter;
import pl.north93.zgame.api.global.uri.impl.router.Routed;

public class UriManagerImpl extends Component implements IUriManager
{
    private final NonorderedRouter router = new NonorderedRouter();

    @Override
    public void register(final String pattern, final IUriCallHandler handler)
    {
        this.getLogger().log(Level.INFO, "Registering new URI pattern. ({0})", pattern);
        this.router.pattern(pattern, handler);
    }

    @Override
    public Object call(final URI uri, final Object... parameter)
    {
        String calledUri = uri.getHost() + uri.getPath();
        if (parameter.length != 0)
        {
            calledUri = MessageFormat.format(calledUri, parameter);
        }

        final Routed routed = this.router.route(calledUri);
        if (routed == null)
        {
            return null;
        }

        return routed.handler().handle(calledUri, routed.params());
    }

    @Override
    public Object call(final String uri, final Object... parameter)
    {
        final String calledUri;
        if (parameter.length == 0)
        {
            calledUri = uri;
        }
        else
        {
            calledUri = MessageFormat.format(uri, parameter);
        }

        final Routed routed = this.router.route(calledUri);
        if (routed == null)
        {
            return null;
        }

        return routed.handler().handle(calledUri, routed.params());
    }

    @Aggregator(UriHandler.class)
    private void handleAnnotation(final IBeanContext context, final Method method, final UriHandler handler)
    {
        this.register(handler.value(), (a1, a2) ->
        {
            try
            {
                return method.invoke(context.getBean(method.getDeclaringClass()), a1, a2);
            }
            catch (final IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("router", this.router).toString();
    }
}
