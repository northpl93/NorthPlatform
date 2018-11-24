package pl.north93.northplatform.api.bukkit.hologui.hologram;

import org.bukkit.entity.ArmorStand;

import pl.north93.northplatform.api.bukkit.entityhider.IEntityHider;

public interface IHologramVisibility
{
    void setup(IEntityHider entityHider, ArmorStand hologramEntity);
}
