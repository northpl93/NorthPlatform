package pl.north93.zgame.api.global.messages;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.utils.Vars;

public class PredefinedVarsTranslatableString extends TranslatableString
{
    private final TranslatableString wrapped;
    private final Vars<Object>       vars;

    public PredefinedVarsTranslatableString(final TranslatableString wrapped, final Vars<Object> vars)
    {
        this.wrapped = wrapped;
        this.vars = vars;
    }

    @Override
    public String getValue(final Locale locale, final Vars<Object> params)
    {
        return this.wrapped.getValue(locale, this.vars.and(params));
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

        final PredefinedVarsTranslatableString that = (PredefinedVarsTranslatableString) o;
        return this.wrapped.equals(that.wrapped) && this.vars.equals(that.vars);
    }

    @Override
    public int hashCode()
    {
        int result = this.wrapped.hashCode();
        result = 31 * result + this.vars.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("wrapped", this.wrapped).append("vars", this.vars).toString();
    }
}
