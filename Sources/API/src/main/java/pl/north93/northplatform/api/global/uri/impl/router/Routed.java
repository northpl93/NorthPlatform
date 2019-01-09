package pl.north93.northplatform.api.global.uri.impl.router;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.uri.IUriCallHandler;

public class Routed
{
    private final IUriCallHandler     target;
    private final boolean             notFound;
    private final Map<String, String> params;

    public Routed(IUriCallHandler target, boolean notFound, Map<String, String> params)
    {
        this.target = target;
        this.notFound = notFound;
        this.params = params;
    }

    public IUriCallHandler handler()
    {
        return this.target;
    }

    public boolean notFound()
    {
        return this.notFound;
    }

    public Map<String, String> params()
    {
        return this.params;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("handler", this.target).append("notFound", this.notFound).append("params", this.params).toString();
    }
}
