package pl.north93.zgame.api.global.messages;

import java.util.Locale;
import java.util.Map;

import pl.north93.zgame.api.global.utils.Vars;

class CustomTranslatableString extends TranslatableString
{
    private final Map<Locale, String> values;

    CustomTranslatableString(final Map<Locale, String> values)
    {
        this.values = values;
    }

    @Override
    public String getValue(final Locale locale, final Vars<Object> params)
    {
        return this.values.getOrDefault(locale, locale.toLanguageTag());
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object instanceof CustomTranslatableString)
        {
            final CustomTranslatableString other = (CustomTranslatableString) object;
            return this.values.equals(other.values);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.values.hashCode();
    }

    @Override
    public String toString()
    {
        return this.values.toString();
    }
}
