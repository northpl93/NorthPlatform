package pl.north93.zgame.api.bukkit.hologui.hologram;

import org.bukkit.Location;

public interface IHologram
{
    void setLine(int line, IHologramLine text);

    void clearLine(int line);

    /**
     * Zwraca odleglosc miedzy kolejnymi linijkami tekstu.
     *
     * @return Odleglosc miedzy linijkami
     */
    double getLinesSpacing();

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
