package pl.north93.zgame.api.bukkit.utils.hologram;

import org.bukkit.Location;

public interface IHologram
{
    void setLine(int line, IHologramLine text);

    void clearLine(int line);

    void remove();

    static IHologram create(final Location location)
    {
        return new HologramImpl(location);
    }
}
