package pl.north93.northplatform.api.bukkit.hologui.hologram.message;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.bukkit.hologui.hologram.HologramRenderContext;
import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologramMessage;

public class LegacyHologramLines implements IHologramMessage
{
    private final TranslatableString[] strings;

    public LegacyHologramLines(final TranslatableString[] strings)
    {
        this.strings = strings;
    }

    @Override
    public List<String> render(final HologramRenderContext renderContext)
    {
        final Locale locale = renderContext.getLocale();
        return Arrays.stream(this.strings)
                     .map(string -> string.getValue(locale).toLegacyText())
                     .collect(Collectors.toList());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("strings", this.strings).toString();
    }
}
