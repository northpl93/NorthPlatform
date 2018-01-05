package pl.north93.zgame.antycheat.utils;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public final class EntityUtils
{
    public static AABB getAABBOfEntityInLocation(final Entity entity, final Location location)
    {
        final net.minecraft.server.v1_10_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();

        final double width = nmsEntity.width / 2d;
        final double height = nmsEntity.length;

        return new AABB(location.getX() - width, location.getY(), location.getZ() - width, location.getX() + width, location.getY() + height, location.getZ() + width);
    }
}
