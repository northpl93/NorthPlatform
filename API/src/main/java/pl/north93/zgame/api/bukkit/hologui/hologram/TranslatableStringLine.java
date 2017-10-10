package pl.north93.zgame.api.bukkit.hologui.hologram;

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
    public String render(final IHologram hologram, final Player player)
    {
        return this.translatableString.getValue(player);
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
