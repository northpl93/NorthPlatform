package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface BlockEmulator
{
    Material getType();

    boolean isApplicable(Block block);

    BlockData getData(Block block);
}
