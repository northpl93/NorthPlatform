package pl.arieals.minigame.elytrarace.arena;

import net.minecraft.server.v1_10_R1.EntityFallingBlock;
import net.minecraft.server.v1_10_R1.IBlockData;
import net.minecraft.server.v1_10_R1.World;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.util.CraftMagicNumbers;

public class NorthFallingBlock extends EntityFallingBlock
{
    public static NorthFallingBlock create(final Location location, final Material material, final byte data)
    {
        final NorthFallingBlock fallingBlock =
                new NorthFallingBlock(
                        ((CraftWorld) location.getWorld()).getHandle(),
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ(),
                        CraftMagicNumbers.getBlock(material).fromLegacyData(data));
        fallingBlock.ticksLived = 1;

        return fallingBlock;
    }

    public NorthFallingBlock(final World world, final double d0, final double d1, final double d2, final IBlockData iblockdata)
    {
        super(world, d0, d1, d2, iblockdata);
        this.setNoGravity(true);
        this.fromMobSpawner = true;
    }

    @Override
    public void m()
    {
        // nothing
    }

    @Override
    public void e(final float f, final float f1)
    {
        // nothing
    }
}
