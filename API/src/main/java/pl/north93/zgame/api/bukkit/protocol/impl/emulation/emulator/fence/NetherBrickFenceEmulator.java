package pl.north93.zgame.api.bukkit.protocol.impl.emulation.emulator.fence;

import org.bukkit.Material;

import pl.north93.zgame.api.bukkit.protocol.impl.emulation.emulator.AbstractFenceEmulator;

public class NetherBrickFenceEmulator extends AbstractFenceEmulator
{
    public NetherBrickFenceEmulator()
    {
        super(Material.NETHER_FENCE, "minecraft:nether_brick_fence");
    }
}
