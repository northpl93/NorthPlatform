package pl.north93.zgame.api.bukkit.hologui.hologram;

import java.util.Locale;

import org.bukkit.entity.Player;

public interface IHologramLine
{
    String render(IHologram hologram, Player player, Locale locale);

    int hashCode();

    boolean equals(Object obj);
}
