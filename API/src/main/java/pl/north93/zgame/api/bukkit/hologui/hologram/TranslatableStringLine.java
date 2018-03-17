package pl.north93.zgame.api.bukkit.hologui.hologram;

import java.util.Locale;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.messages.TranslatableString;

public class TranslatableStringLine implements IHologramLine
{
    private final TranslatableString translatableString;

    public TranslatableStringLine(final TranslatableString translatableString)
    {
        this.translatableString = translatableString;
    }

    @Override
    public String render(final IHologram hologram, final Player player, final Locale locale)
    {
        // getValue zwraca nieprzekonwertowany tekst.
        return this.translatableString.getValue(locale).toLegacyText();
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

        final TranslatableStringLine that = (TranslatableStringLine) o;
        return this.translatableString.equals(that.translatableString);
    }

    @Override
    public int hashCode()
    {
        return this.translatableString.hashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("translatableString", this.translatableString).toString();
    }
}
