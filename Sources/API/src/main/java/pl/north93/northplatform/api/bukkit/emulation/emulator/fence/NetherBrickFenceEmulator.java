package pl.north93.northplatform.api.bukkit.emulation.emulator.fence;

import org.bukkit.Material;

import pl.north93.northplatform.api.bukkit.emulation.emulator.AbstractFenceEmulator;

public class NetherBrickFenceEmulator extends AbstractFenceEmulator
{
    public NetherBrickFenceEmulator()
    {
        super(Material.NETHER_FENCE, "minecraft:nether_brick_fence");
    }
}
