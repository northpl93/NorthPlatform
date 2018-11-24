package pl.north93.northplatform.api.bukkit.hologui.hologram;

import java.util.Locale;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class HologramRenderContext
{
    private final IHologram hologram;
    private final Player    player;
    private final Locale    locale;

    public HologramRenderContext(final IHologram hologram, final Player player, final Locale locale)
    {
        this.hologram = hologram;
        this.player = player;
        this.locale = locale;
    }

    public IHologram getHologram()
    {
        return this.hologram;
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public Locale getLocale()
    {
        return this.locale;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("hologram", this.hologram).append("player", this.player).append("locale", this.locale).toString();
    }
}
