package pl.north93.zgame.api.global.messages;

import java.util.Locale;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.utils.Vars;

@ToString
@EqualsAndHashCode(callSuper = false) // BaseComponent nie ma equals/hashCode, wiec czy to ma sens?
class ConstantTranslatableString extends TranslatableString
{
    public static final TranslatableString EMPTY = new ConstantTranslatableString(""); // uzywane w TranslatableString#empty()
    private final BaseComponent fixedValue;

    ConstantTranslatableString(final String legacyText)
    {
        this(ChatUtils.fromLegacyText(legacyText));
    }

    ConstantTranslatableString(final BaseComponent fixedValue)
    {
        this.fixedValue = fixedValue;
    }

    @Override
    protected BaseComponent generateComponent(final Locale locale, final Vars<Object> params)
    {
        return this.fixedValue;
    }

    @Override
    protected String generateString(final Locale locale, final Vars<Object> params)
    {
        return this.fixedValue.toLegacyText();
    }
}
