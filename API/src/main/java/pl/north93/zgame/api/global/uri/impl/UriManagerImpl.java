package pl.north93.zgame.api.global.uri.impl;

import java.net.URI;
import java.text.MessageFormat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.uri.IUriCallHandler;
import pl.north93.zgame.api.global.uri.IUriManager;
import pl.north93.zgame.api.global.uri.impl.router.NonorderedRouter;
import pl.north93.zgame.api.global.uri.impl.router.Routed;

public class UriManagerImpl extends Component implements IUriManager
{
    private final NonorderedRouter router = new NonorderedRouter();

    @Override
    public void register(final String pattern, final IUriCallHandler handler)
    {
        this.router.pattern(pattern, handler);
    }

    @Override
    public Object call(final URI uri, final Object... parameter)
    {
        final String calledUri;
        if (parameter.length == 0)
        {
            calledUri = uri.getPath();
        }
        else
        {
            calledUri = MessageFormat.format(uri.getPath(), parameter);
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
