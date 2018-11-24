package pl.north93.northplatform.api.global.uri.impl;

import static java.text.MessageFormat.format;


import java.lang.reflect.Method;
import java.net.URI;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.uri.impl.router.NonorderedRouter;
import pl.north93.northplatform.api.global.uri.impl.router.Routed;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.uri.IUriCallHandler;
import pl.north93.northplatform.api.global.uri.IUriManager;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;

@Slf4j
public class UriManagerImpl extends Component implements IUriManager
{
    private final NonorderedRouter router = new NonorderedRouter();

    @Override
    public void register(final String pattern, final IUriCallHandler handler)
    {
        log.info("Registering new URI pattern. ({})", pattern);
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
            log.warn("Not found route for {}", calledUri);
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
                log.error("Cant execute {} for uri {}", method, handler.value(), e);
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
