package pl.north93.northplatform.api.global.utils.math;

public final class MathUtils
{
    // angle trzeba podawac w radianach
    //
    // double delta = Math.toRadians(YAW_KTORY_LICZYMY);
    // boolean clockwise = true;
    // if (delta < 0)
    // {
    //     clockwise = false;
    //     delta = -delta;
    // }
    public static Point rotatePoint(final boolean clockwise, final double cx, final double cy, final double angle, double pointX, double pointZ)
    {
        final double s = Math.sin(angle);
        final double c = Math.cos(angle);

        // translate point back to origin:
        pointX -= cx;
        pointZ -= cy;

        // rotate point
        final double xnew;
        final double ynew;
        if (clockwise)
        {
            xnew = pointX * c - pointZ * s;
            ynew = pointX * s + pointZ * c;
        }
        else
        {
            xnew = pointX * c + pointZ * s;
            ynew = -pointX * s + pointZ * c;
        }

        // translate point back:
        pointX = xnew + cx;
        pointZ = ynew + cy;
        return new Point(pointX, pointZ);
    }

    public static double square(final double num)
    {
        return num * num;
    }

    public static double distanceSquared(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2)
    {
        return square(x1 - x2) + square(y1 - y2) + square(z1 - z2);
    }

    // From nms.MathHelper (static int d(float var0))
    public static int floor(final float num)
    {
        final int var1 = (int) num;
        return num < (float) var1 ? var1 - 1 : var1;
    }

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
