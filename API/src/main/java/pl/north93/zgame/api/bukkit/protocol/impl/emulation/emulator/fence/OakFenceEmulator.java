package pl.north93.zgame.api.bukkit.protocol.impl.emulation.emulator.fence;

import org.bukkit.Material;

import pl.north93.zgame.api.bukkit.protocol.impl.emulation.emulator.AbstractFenceEmulator;

public class OakFenceEmulator extends AbstractFenceEmulator
{
    public OakFenceEmulator()
    {
        super(Material.FENCE, "minecraft:oak_fence");
    }
}
