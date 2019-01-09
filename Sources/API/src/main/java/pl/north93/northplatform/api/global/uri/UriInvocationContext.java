package pl.north93.northplatform.api.global.uri;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteMathUtils;

/**
 * Reprezentuje kontekst w którym wykonywane jest dane URI.
 * Handler musi przyjmować obiekt tej klasy jako argument.
 */
public final class UriInvocationContext
{
    private final String calledUri;
    private final Map<String, String> params;

    public UriInvocationContext(final String calledUri, final Map<String, String> params)
    {
        this.calledUri = calledUri;
        this.params = params;
    }

    public String getCalledUri()
    {
        return this.calledUri;
    }

    public String asString(final String field)
    {
        return this.params.get(field);
    }

    public Integer asInteger(final String field)
    {
        return DioriteMathUtils.asInt(this.params.get(field));
    }

    public UUID asUuid(final String field)
    {
        return UUID.fromString(this.params.get(field));
    }

    public Map<String, String> rawParameters()
    {
        return this.params;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("calledUri", this.calledUri).toString();
    }
}
