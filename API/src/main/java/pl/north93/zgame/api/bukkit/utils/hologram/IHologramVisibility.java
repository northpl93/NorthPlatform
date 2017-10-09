package pl.north93.zgame.api.bukkit.utils.hologram;

import org.bukkit.entity.ArmorStand;

import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;

public interface IHologramVisibility
{
    void setup(IEntityHider entityHider, ArmorStand hologramEntity);
}
