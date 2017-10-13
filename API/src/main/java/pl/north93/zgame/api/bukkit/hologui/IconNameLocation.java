package pl.north93.zgame.api.bukkit.hologui;

import org.bukkit.Location;

public enum IconNameLocation
{
    BELOW
            {
                @Override
                public Location calculate(final IIcon icon)
                {
                    final Location center = icon.getHoloContext().getCenter();
                    return icon.getPosition().calculateTarget(center).add(0, -0.4, 0);
                }
            },
    ABOVE
            {
                @Override
                public Location calculate(final IIcon icon)
                {
                    final Location center = icon.getHoloContext().getCenter();

                    final int additionalLines = icon.getDisplayName().length - 1;
                    final double additionalSpace = additionalLines * 0.3;

                    final double finalY = (5 / 8D) + additionalSpace;
                    return icon.getPosition().calculateTarget(center).add(0, finalY, 0);
                }
            };

    public abstract Location calculate(final IIcon icon);
}
