package pl.north93.zgame.antycheat.utils;

import static pl.north93.zgame.antycheat.utils.EntityUtils.getAABBOfEntityInLocation;


import java.util.List;

import net.minecraft.server.v1_10_R1.AxisAlignedBB;
import net.minecraft.server.v1_10_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Entity;

public final class DistanceUtils
{
    /**
     * Oblicza dystans danego entity w podanej lokalizacji do podlogi.
     * Z entity pobierany jest tylko jego AABB, lokalizacja jako drugi argument
     * mozna podac dowolna. Ostatni argument powieksza AAB w dlugosci i szerokosci.
     * Nie uwzglednia cieczy.
     *
     * @param entity Entity dla ktorego obliczamy odleglosc.
     * @param location Lokacja w ktorej znajduje sie entity.
     * @param growAABBSurface O ile powiekszyc wartosci X i Z bounding boxa.
     * @return Odleglosc entity w danej lokalizacji od gruntu.
     */
    public static double entityDistanceToGround(final Entity entity, final Location location, final double growAABBSurface)
    {
        final WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

        // obliczamy AABB danego entity w danej lokalizacji i go powiekszamy o podana wartosc
        final AABB boundingBox = getAABBOfEntityInLocation(entity, location).grow(growAABBSurface, 0, growAABBSurface);
        // poserzamy AABB do samego dolu mapy i zamieniamy od razu na minecraftowy AxisAlignedBB
        final AxisAlignedBB targetBB = new AxisAlignedBB(boundingBox.minX, 0, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        // Delegujemy liczenie kolizji do kodu minecrafta
        final List<AxisAlignedBB> collidingBlocks = worldServer.a(targetBB); // getCollisionBoxes in MCP

        double y = 0;
        for (final AxisAlignedBB cube : collidingBlocks)
        {
            y = Math.max(y, cube.e); // cube.e = maxY
        }

        return location.getY() - y;
    }

    // wywoluje powyzsza metode z growSurface = 0, wiec przewidywane domyslne dzialanie
    public static double entityDistanceToGround(final Entity entity, final Location location)
    {
        return entityDistanceToGround(entity, location, 0);
    }
}
