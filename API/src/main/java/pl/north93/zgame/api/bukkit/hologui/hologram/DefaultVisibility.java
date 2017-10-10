package pl.north93.zgame.api.bukkit.hologui.hologram;

import org.bukkit.entity.ArmorStand;

import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;

public class DefaultVisibility implements IHologramVisibility
{
    public static final DefaultVisibility INSTANCE = new DefaultVisibility();

    @Override
    public void setup(final IEntityHider entityHider, final ArmorStand hologramEntity)
    {
        // nic nie musimy tu robic
    }
}
