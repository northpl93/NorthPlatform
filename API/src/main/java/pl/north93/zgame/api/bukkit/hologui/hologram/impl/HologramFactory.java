package pl.north93.zgame.api.bukkit.hologui.hologram.impl;

import org.bukkit.Location;

import pl.north93.zgame.api.bukkit.hologui.hologram.DefaultVisibility;
import pl.north93.zgame.api.bukkit.hologui.hologram.IHologram;
import pl.north93.zgame.api.bukkit.hologui.hologram.IHologramVisibility;

/**
 * Statyczne metody tworzące nowe instancje {@link IHologram}.
 */
public final class HologramFactory
{
    private HologramFactory()
    {
    }

    public static IHologram create(final Location location)
    {
        return create(DefaultVisibility.INSTANCE, location);
    }

    public static IHologram create(final IHologramVisibility hologramVisibility, final Location location)
    {
        return new HologramImpl(hologramVisibility, location);
    }
}
