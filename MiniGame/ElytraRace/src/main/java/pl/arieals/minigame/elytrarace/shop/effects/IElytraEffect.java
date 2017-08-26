package pl.arieals.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.utils.math.MathUtils;
import pl.north93.zgame.api.global.utils.math.Point;

public interface IElytraEffect
{
    String name();

    void play(Player player);

    default void calculateLegsLocation(final Location location)
    {
        this.calculateLegsLocation(location, 1);
    }

    default void calculateLegsLocation(final Location location, final double distance)
    {
        double delta = Math.toRadians(location.getYaw());
        boolean clockwise = true;
        if (delta < 0)
        {
            clockwise = false;
            delta = -delta;
        }

        final Point point = MathUtils.rotatePoint(clockwise,
                location.getX(), location.getZ(),
                delta,
                location.getX(), location.getZ() - distance);

        location.setX(point.x);
        location.setZ(point.z);
    }
}
