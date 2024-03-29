package pl.north93.northplatform.antycheat.utils;

import static pl.north93.northplatform.antycheat.utils.EntityUtils.getAABBOfEntityInLocation;


import java.util.List;

import org.bukkit.entity.Entity;

import pl.north93.northplatform.antycheat.utils.handle.WorldHandle;
import pl.north93.northplatform.antycheat.utils.location.IPosition;

public final class DistanceUtils
{
    /**
     * Oblicza dystans danego entity w podanej lokalizacji do podlogi.
     * Z entity pobierany jest tylko jego AABB, lokalizacja jako drugi argument
     * mozna podac dowolna. Ostatni argument powieksza AABB w dlugosci i szerokosci.
     * Nie uwzglednia cieczy.
     *
     * @param entity Entity dla ktorego obliczamy odleglosc.
     * @param position Lokacja w ktorej znajduje sie entity.
     * @param growAABBSurface O ile powiekszyc wartosci X i Z bounding boxa.
     * @return Odleglosc entity w danej lokalizacji od gruntu.
     */
    public static double entityDistanceToGround(final Entity entity, final IPosition position, final double growAABBSurface)
    {
        final WorldHandle world = position.getWorldHandle();

        // obliczamy AABB danego entity w danej lokalizacji i go powiekszamy o podana wartosc
        final AABB boundingBox = getAABBOfEntityInLocation(entity, position).grow(growAABBSurface, 0, growAABBSurface);
        // poserzamy AABB do samego dolu mapy
        final AABB targetBB = new AABB(boundingBox.minX, 0, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        // Delegujemy liczenie kolizji do kodu minecrafta
        final List<AABB> collidingBlocks = world.getCollisionBoxes(targetBB);

        double y = 0;
        for (final AABB cube : collidingBlocks)
        {
            y = Math.max(y, cube.maxY);
        }

        return position.getY() - y;
    }

    // wywoluje powyzsza metode z growSurface = 0, wiec przewidywane domyslne dzialanie
    public static double entityDistanceToGround(final Entity entity, final IPosition location)
    {
        return entityDistanceToGround(entity, location, 0);
    }

    public static double xzDistance(final IPosition p1, final IPosition p2)
    {
        final double xD = Math.abs(p1.getX() - p2.getX());
        final double zD = Math.abs(p1.getZ() - p2.getZ());
        return Math.sqrt(xD * xD + zD * zD);
    }
}
