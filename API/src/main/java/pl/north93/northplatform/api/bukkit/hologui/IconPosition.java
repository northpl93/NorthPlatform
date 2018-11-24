package pl.north93.northplatform.api.bukkit.hologui;

import org.bukkit.Location;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.utils.math.MathUtils;
import pl.north93.northplatform.api.global.utils.math.Point;

public final class IconPosition
{
    private final double distance;
    private final double angle;
    private final double height;

    public IconPosition(final double distance, final double angle, final double height)
    {
        this.distance = distance;
        this.angle = angle;
        this.height = height;
    }

    public double getDistance()
    {
        return this.distance;
    }

    public double getAngle()
    {
        return this.angle;
    }

    public double getHeight()
    {
        return this.height;
    }

    /**
     * Oblicza lokalizacje na podstawie podanego srodkowego punktu.
     * Instancja podana w argumencie nie jest modyfikowana.
     *
     * @param location Lokalizacja srodka.
     * @return Przeliczona pozycja.
     */
    public Location calculateTarget(final Location location)
    {
        final double targetAngle = location.getYaw() + this.angle + 90;

        double delta = Math.toRadians(targetAngle);
        boolean clockwise = true;
        if (delta < 0)
        {
            clockwise = false;
            delta = - delta;
        }

        final double targetPointX = location.getX() + this.distance;
        final double targetPointZ = location.getZ();
        final Point point = MathUtils.rotatePoint(clockwise, location.getX(), location.getZ(), delta, targetPointX, targetPointZ);

        final Location result = new Location(location.getWorld(), point.x, location.getY() + this.height, point.z, 0, 0F);
        result.setDirection(location.toVector().subtract(result.toVector())); //set the origin's direction to be the direction vector between point A and B.

        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("distance", this.distance).append("angle", this.angle).append("height", this.height).toString();
    }
}
