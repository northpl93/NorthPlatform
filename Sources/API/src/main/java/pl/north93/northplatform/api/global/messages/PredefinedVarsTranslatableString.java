package pl.north93.northplatform.api.global.messages;

import java.util.Locale;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.global.utils.Vars;

@ToString
@EqualsAndHashCode(callSuper = false)
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
    protected BaseComponent generateComponent(final Locale locale, final Vars<Object> params)
    {
        return this.wrapped.generateComponent(locale, this.vars.and(params));
    }

    @Override
    protected String generateString(final Locale locale, final Vars<Object> params)
    {
        return this.wrapped.generateString(locale, this.vars.and(params));
    }
}
