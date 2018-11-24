package pl.north93.northplatform.api.bukkit.emulation;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface BlockEmulator
{
    Material getType();

    boolean isApplicable(Block block);

    BlockData getData(Block block);
}
