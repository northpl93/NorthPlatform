package pl.north93.zgame.api.bukkit.hologui.hologram.impl;

import org.bukkit.Location;

import pl.north93.zgame.api.bukkit.hologui.hologram.DefaultVisibility;
import pl.north93.zgame.api.bukkit.hologui.hologram.IHologram;
import pl.north93.zgame.api.bukkit.hologui.hologram.IHologramVisibility;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/**
 * Statyczne metody tworzące nowe instancje {@link IHologram}.
 */
public final class HologramFactory
{
    @Inject
    private static HologramManager hologramManager;

    private HologramFactory()
    {
    }

    public static IHologram create(final Location location)
    {
        return create(DefaultVisibility.INSTANCE, location);
    }

    public static IHologram create(final IHologramVisibility hologramVisibility, final Location location)
    {
        return hologramManager.createHologram(hologramVisibility, location);
    }
}
