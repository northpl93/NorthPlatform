package pl.north93.northplatform.antycheat.utils.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.antycheat.utils.handle.WorldHandle;

public interface IPosition
{
    World getWorld();

    default WorldHandle getWorldHandle()
    {
        return WorldHandle.of(this.getWorld());
    }

    double getX();

    double getY();

    double getZ();

    float getYaw();

    float getPitch();

    Vector getDirection();

    default Location toBukkit()
    {
        return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

    static IPosition fromBukkit(final Location location)
    {
        return new BukkitLocationWrapper(location);
    }
}

class BukkitLocationWrapper implements IPosition
{
    private final Location location;

    public BukkitLocationWrapper(final Location location)
    {
        this.location = location;
    }

    @Override
    public World getWorld()
    {
        return this.location.getWorld();
    }

    @Override
    public double getX()
    {
        return this.location.getX();
    }

    @Override
    public double getY()
    {
        return this.location.getY();
    }

    @Override
    public double getZ()
    {
        return this.location.getZ();
    }

    @Override
    public float getYaw()
    {
        return this.location.getYaw();
    }

    @Override
    public float getPitch()
    {
        return this.location.getPitch();
    }

    @Override
    public Vector getDirection()
    {
        return this.location.getDirection();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("location", this.location).toString();
    }
}