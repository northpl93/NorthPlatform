package pl.north93.northplatform.antycheat.utils;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;

import org.bukkit.util.Vector;

public final class AABB
{
    public final double minX, minY, minZ;
    public final double maxX, maxY, maxZ;

    public AABB(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ)
    {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public AABB(final AxisAlignedBB nms)
    {
        this(nms.a, nms.b, nms.c, nms.d, nms.e, nms.f);
    }

    /**
     * Tworzy nowy AABB na podstawie NMSowego z danym offsetem.
     *
     * @param nms NMSowy AxisAlignedBB.
     * @param offsetX Wartość dodana na osi X.
     * @param offsetY Wartość dodana na osi Y.
     * @param offsetZ Wartość dodana na osi Z.
     */
    public AABB(final AxisAlignedBB nms, final double offsetX, final double offsetY, final double offsetZ)
    {
        this(nms.a + offsetX, nms.b + offsetY, nms.c + offsetZ, nms.d + offsetX, nms.e + offsetY, nms.f + offsetZ);
    }

    public AxisAlignedBB toNms()
    {
        return new AxisAlignedBB(this.minX, this.minY,  this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public AABB grow(final double x, final double y, final double z)
    {
        return new AABB(this.minX - x, this.minY - y, this.minZ - z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public AABB offset(final double x, final double y, final double z)
    {
        return new AABB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public boolean intersects(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2)
    {
        return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1 && this.minZ < z2 && this.maxZ > z1;
    }

    public boolean intersects(final AABB other)
    {
        return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public boolean intersects(final Vector position)
    {
        if (position.getX() < this.minX || position.getX() > this.maxX)
        {
            return false;
        }
        else if (position.getY() < this.minY || position.getY() > this.maxY)
        {
            return false;
        }
        else if (position.getZ() < this.minZ || position.getZ() > this.maxZ)
        {
            return false;
        }
        return true;
    }

    public boolean nonIntersects(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2)
    {
        return this.minX > x2 && this.maxX < x1 && this.minY > y2 && this.maxY < y1 && this.minZ > z2 && this.maxZ < z1;
    }

    public boolean nonIntersects(final AABB other)
    {
        return this.nonIntersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public Vector minPointVector()
    {
        return new Vector(this.minX, this.minY, this.minZ);
    }

    public Vector maxPointVector()
    {
        return new Vector(this.maxX, this.maxY, this.maxZ);
    }

    public Vector middlePointVector()
    {
        final double x = (this.maxX - this.minX) * 0.5;
        final double y = (this.maxY - this.minY) * 0.5;
        final double z = (this.maxZ - this.minZ) * 0.5;
        return new Vector(x, y, z);
    }

    /**
     * Adds a coordinate to the bounding box, extending it if the point lies outside the current ranges.
     *
     * @return Modified bounding box.
     */
    public AABB addCoord(final double x, final  double y, final double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D)
        {
            d0 += x;
        }
        else if (x > 0.0D)
        {
            d3 += x;
        }

        if (y < 0.0D)
        {
            d1 += y;
        }
        else if (y > 0.0D)
        {
            d4 += y;
        }

        if (z < 0.0D)
        {
            d2 += z;
        }
        else if (z > 0.0D)
        {
            d5 += z;
        }

        return new AABB(d0, d1, d2, d3, d4, d5);
    }
}
