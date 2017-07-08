package pl.north93.zgame.api.bukkit.utils.nms;

import net.minecraft.server.v1_10_R1.EntityFallingBlock;
import net.minecraft.server.v1_10_R1.IBlockData;
import net.minecraft.server.v1_10_R1.World;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.util.CraftMagicNumbers;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NorthFallingBlock extends EntityFallingBlock
{
    public static NorthFallingBlock createDerped(final Location location, final Material material, final byte data)
    {
        final NorthFallingBlock fallingBlock =
                new NorthFallingBlock(
                        ((CraftWorld) location.getWorld()).getHandle(),
                        location.getBlockX() + 0.5,
                        location.getBlockY(), // poprawiane jest automatycznie
                        location.getBlockZ() + 0.5,
                        CraftMagicNumbers.getBlock(material).fromLegacyData(data), true);
        fallingBlock.ticksLived = 1;

        return fallingBlock;
    }

    public static NorthFallingBlock createNormal(final Location location, final Material material, final byte data)
    {
        final NorthFallingBlock fallingBlock =
                new NorthFallingBlock(
                                             ((CraftWorld) location.getWorld()).getHandle(),
                                             location.getBlockX() + 0.5,
                                             location.getBlockY(), // poprawiane jest automatycznie
                                             location.getBlockZ() + 0.5,
                                             CraftMagicNumbers.getBlock(material).fromLegacyData(data), false);
        fallingBlock.ticksLived = 1;

        return fallingBlock;
    }

    private final boolean derped;
    public NorthFallingBlock(final World world, final double d0, final double d1, final double d2, final IBlockData iblockdata, final boolean derped)
    {
        super(world, d0, d1, d2, iblockdata);
        this.derped = derped;
        if (derped)
        {
            this.setNoGravity(true);
            this.fromMobSpawner = true;
        }
    }

    @Override
    public void m()
    {
        if (this.derped)
        {
            return; // nothing
        }
        super.m();
    }

    @Override
    public void e(final float f, final float f1)
    {
        if (this.derped)
        {
            return; // nothing
        }
        super.e(f, f1);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("derped", this.derped).toString();
    }
}
