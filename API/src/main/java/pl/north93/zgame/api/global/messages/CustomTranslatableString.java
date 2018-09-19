package pl.north93.zgame.api.global.messages;

import java.util.Locale;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.utils.Vars;

@ToString
@EqualsAndHashCode(callSuper = false)
class CustomTranslatableString extends TranslatableString
{
    private final Map<Locale, String> values;

    CustomTranslatableString(final Map<Locale, String> values)
    {
        this.values = values;
    }

    @Override
    protected BaseComponent generateComponent(final Locale locale, final Vars<Object> params)
    {
        final String legacyText = this.values.getOrDefault(locale, locale.toLanguageTag());
        return ChatUtils.fromLegacyText(legacyText);
    }

    @Override
    protected String generateString(final Locale locale, final Vars<Object> params)
    {
        return this.values.getOrDefault(locale, locale.toLanguageTag());
    }
}
