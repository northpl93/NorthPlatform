package pl.north93.zgame.api.global.uri.impl;

import static java.text.MessageFormat.format;


import java.lang.reflect.Method;
import java.net.URI;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.uri.IUriCallHandler;
import pl.north93.zgame.api.global.uri.IUriManager;
import pl.north93.zgame.api.global.uri.UriHandler;
import pl.north93.zgame.api.global.uri.UriInvocationContext;
import pl.north93.zgame.api.global.uri.impl.router.NonorderedRouter;
import pl.north93.zgame.api.global.uri.impl.router.Routed;

public class UriManagerImpl extends Component implements IUriManager
{
    private final NonorderedRouter router = new NonorderedRouter();

    @Override
    public void register(final String pattern, final IUriCallHandler handler)
    {
        this.getLogger().log(Level.INFO, "[UriManagerImpl] Registering new URI pattern. ({0})", pattern);
        this.router.pattern(pattern, handler);
    }

    @Override
    public Object call(final URI uri, final Object... parameter)
    {
        String calledUri = uri.getHost() + uri.getPath();
        if (parameter.length != 0)
        {
            calledUri = format(calledUri, parameter);
        }

        return this.invokeUri(calledUri, parameter);
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
            calledUri = format(uri, parameter);
        }

        return this.invokeUri(calledUri, parameter);
    }

    private Object invokeUri(final String calledUri, final Object[] parameters)
    {
        final Routed routed = this.router.route(calledUri);
        if (routed == null)
        {
            this.getLogger().log(Level.WARNING, "[UriManagerImpl] Not found route for {0}", calledUri);
            return null;
        }

        final UriInvocationContext context = new UriInvocationContext(calledUri, routed.params());
        return routed.handler().handle(context);
    }

    @Aggregator(UriHandler.class)
    private void handleAnnotation(final @Named("MethodOwner") Object methodOwner, final Method method, final UriHandler handler)
    {
        this.register(handler.value(), context ->
        {
            try
            {
                method.setAccessible(true);
                if (method.getParameterCount() == 1)
                {
                    return method.invoke(methodOwner, context);
                }

                // todo LEGACY, zastępujemy osobne parametry jednym kontekstem. W przyszłości if i linijka poniżej do usunięcia
                return method.invoke(methodOwner, context.getCalledUri(), context.rawParameters());
            }
            catch (final Exception e)
            {
                this.getLogger().log(Level.SEVERE, format("[UriManagerImpl] Cant execute {0} for uri {1}", method, handler.value()), e);
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
