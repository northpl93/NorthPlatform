package pl.north93.zgame.api.bukkit.utils.hologram;

import org.bukkit.Location;

public interface IHologram
{
    void setLine(int line, IHologramLine text);

    void clearLine(int line);

    void remove();

    static IHologram create(final Location location)
    {
        return create(DefaultVisibility.INSTANCE, location);
    }

    static IHologram create(final IHologramVisibility hologramVisibility, final Location location)
    {
        return new HologramImpl(hologramVisibility, location);
    }
}
