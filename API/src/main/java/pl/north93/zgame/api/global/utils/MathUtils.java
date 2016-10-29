package pl.north93.zgame.api.global.utils;

public final class MathUtils
{
    // https://bukkit.org/threads/get-player-in-line-of-sight.146323/#post-1664622
    private boolean hasIntersection(final Vector3D p1, final Vector3D p2, final Vector3D min, final Vector3D max)
    {
        final double epsilon = 0.0001f;

        final Vector3D d = p2.subtract(p1).multiply(0.5);
        final Vector3D e = max.subtract(min).multiply(0.5);
        final Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        final Vector3D ad = d.abs();

        return Math.abs(c.x) <= e.x + ad.x && Math.abs(c.y) <= e.y + ad.y && Math.abs(c.z) <= e.z + ad.z && Math.abs(d.y * c.z - d.z * c.y) <= e.y * ad.z + e.z * ad.y + epsilon && Math.abs(d.z * c.x - d.x * c.z) <= e.z * ad.x + e.x * ad.z + epsilon && Math.abs(d.x * c.y - d.y * c.x) <= e.x * ad.y + e.y * ad.x + epsilon;
    }
}
