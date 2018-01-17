package pl.north93.zgame.antycheat.utils;

import java.util.List;

import net.minecraft.server.v1_12_R1.EntityBoat;
import net.minecraft.server.v1_12_R1.EntityShulker;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import pl.north93.zgame.antycheat.utils.location.IPosition;

public final class EntityUtils
{
    public static AABB getAABBOfEntityInLocation(final Entity entity, final IPosition position)
    {
        final net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();

        final double width = nmsEntity.width / 2d;
        final double height = nmsEntity.length;

        return new AABB(position.getX() - width, position.getY(), position.getZ() - width, position.getX() + width, position.getY() + height, position.getZ() + width);
    }

    /**
     * Sprawdza czy dane entity o podanym AABB stoi na innym entity.
     * Przydatne przy sprawdzaniu czy np. gracz stoi na lodce.
     *
     * @param entity
     * @param aabb
     * @return True jesli stoimy na jakims entity.
     */
    public static boolean standsOnEntity(final Entity entity, final AABB aabb)
    {
        final net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();

        // powiekszamy AABB
        final double aabbCheckEpsilon = 0.25;
        final AABB targetAabb = aabb.grow(aabbCheckEpsilon, aabbCheckEpsilon, aabbCheckEpsilon);

        // pytamy minecrafta o liste kolidujacych entities
        final List<net.minecraft.server.v1_12_R1.Entity> entities = nmsEntity.world.getEntities(nmsEntity, targetAabb.toNms());
        for (final net.minecraft.server.v1_12_R1.Entity collidingEntity : entities)
        {
            // sprawdzamy czy kolidujemy z lódką lub shulkerem
            if (nmsEntity == collidingEntity || ! (collidingEntity instanceof EntityBoat) && ! (collidingEntity instanceof EntityShulker))
            {
                continue;
            }

            if (aabb.minY >= collidingEntity.locY && aabb.minY - collidingEntity.locY <= 0.7)
            {
                return true;
            }

            // nie wiem po co to, ale skopiowane z NCP
            if (targetAabb.nonIntersects(new AABB(collidingEntity.getBoundingBox())))
            {
                continue;
            }

            return true;
        }

        return false;
    }

    public static boolean standsOnEntity(final Entity entity)
    {
        final AABB aabbOfEntityInLocation = getAABBOfEntityInLocation(entity, IPosition.fromBukkit(entity.getLocation()));
        return standsOnEntity(entity, aabbOfEntityInLocation);
    }
}
