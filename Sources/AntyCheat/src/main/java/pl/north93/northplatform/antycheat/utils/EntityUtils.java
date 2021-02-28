package pl.north93.northplatform.antycheat.utils;

import static org.apache.commons.lang3.ArrayUtils.contains;

import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.toNmsEntity;


import java.util.List;

import net.minecraft.server.v1_12_R1.EntityBoat;
import net.minecraft.server.v1_12_R1.EntityShulker;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import pl.north93.northplatform.antycheat.utils.block.CollidingBlocksIterator;
import pl.north93.northplatform.antycheat.utils.location.IPosition;

public final class EntityUtils
{
    public static AABB getAABBOfEntityInLocation(final Entity entity, final IPosition position)
    {
        final net.minecraft.server.v1_12_R1.Entity nmsEntity = toNmsEntity(entity);

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
        final net.minecraft.server.v1_12_R1.Entity nmsEntity = toNmsEntity(entity);

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

    public static boolean isStandsOn(final Entity entity, final IPosition position, final Material... materials)
    {
        final AABB entityAabb = getAABBOfEntityInLocation(entity, position);
        final AABB underEntity = new AABB(entityAabb.minX, entityAabb.minY - 1, entityAabb.minZ, entityAabb.maxX, entityAabb.minY, entityAabb.maxZ);

        final CollidingBlocksIterator iterator = new CollidingBlocksIterator(entity.getWorld(), underEntity);
        while (iterator.hasNext())
        {
            final Block block = iterator.next();
            if (contains(materials, block.getType()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Oblicza maksymalną wysokość jaką osiągnie entity po zaaplikowaniu velocity na Y
     * o podanej wartości.
     *
     * @param vel Startowe velocity na osi Y.
     * @return Maksymalna wysokość jaką osiągnie entity od punktu zaaplikowania velocity.
     */
    public static double maxHeightByStartVelocity(double vel)
    {
        double height = 0;
        while (vel > 0)
        {
            height += vel;
            vel = (vel - 0.08) * 0.98;
        }
        return height;
    }

    public static double maxDistanceByStartVelocity(double vel)
    {
        double distance = 0;
        while (Math.abs(vel) > 0.003D) // EntityLivingBase onLivingUpdate w MCP1.10
        {
            distance += vel;
            vel *= 0.91; // moveEntityWithHeading this.motionX *= (double)f6; gdzie f6 to śliskość bloki, powietrze 0.91
        }
        return distance;
    }
}
