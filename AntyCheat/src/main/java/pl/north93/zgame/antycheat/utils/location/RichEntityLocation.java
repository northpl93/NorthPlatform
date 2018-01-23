package pl.north93.zgame.antycheat.utils.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import org.diorite.utils.lazy.LazyValue;

import pl.north93.zgame.antycheat.utils.AABB;
import pl.north93.zgame.antycheat.utils.DistanceUtils;
import pl.north93.zgame.antycheat.utils.EntityUtils;
import pl.north93.zgame.antycheat.utils.block.BlockUtils;

public final class RichEntityLocation implements IPosition
{
    private final Entity   entity;
    private final Location location;
    private final AABB     aabb;
    private final Vector   velocity;

    /** Określa dystans dzielący entity i twardy grunt */
    private final LazyValue<Double>  distanceToGround;

    /** Określa czy entity stoi na innym entity */
    private final LazyValue<Boolean> standsOnEntity;

    /** Wszystkie flagi blogów obowiązujące bloki kolidująca z AABB */
    private final LazyValue<Long>    flags;

    /**
     * Tworzy nową instancję RichEntityLocation dla danego entity będącego w określonej lokalizacji.
     *
     * @param entity
     * @param location
     */
    public RichEntityLocation(final Entity entity, final Location location)
    {
        this.entity = entity;
        this.location = location;
        this.velocity = entity.getVelocity();
        this.aabb = EntityUtils.getAABBOfEntityInLocation(entity, this); // mozna przekazac bezpiecznie this bo location jest juz ustawione

        this.distanceToGround = new LazyValue<>(this::computeDistanceToGround);
        this.standsOnEntity = new LazyValue<>(this::computeStandsOnEntity);
        this.flags = new LazyValue<>(this::computeFlags);
    }

    // = = = Głowne metody logiczne. = = = //

    /**
     * Sprawdza czy entity stoi na ziemi, czyli dzieli je od niego dystans 0 blokow.
     *
     * @return True jesli entity stoi na twardym gruncie
     */
    public boolean isStandsOnGround()
    {
        return this.getDistanceToGround() <= 0;
    }

    /**
     * Sprawdza czy entity stoi na ziemi lub na innym entity.
     *
     * @return True jesli entity stoi, nie lata.
     */
    public boolean isStands()
    {
        return this.isStandsOnGround() || this.isStandsOnEntity();
    }

    /**
     * Pobiera velocity które miało nadane entity w momencie utworzenia instancji RichEntityLocation.
     *
     * @return Velocity entity w momencie utworzenia instancji tej klasy.
     */
    public Vector getVelocity()
    {
        return this.velocity;
    }

    public Vector vectorToOther(final IPosition position)
    {
        return new Vector(position.getX() - this.getX(), position.getY() - this.getY(), position.getZ() - this.getZ());
    }

    // = = = Metody zwracające zcachowane dane i generujące cache = = = //

    public double getDistanceToGround()
    {
        return this.distanceToGround.get();
    }

    private double computeDistanceToGround()
    {
        return DistanceUtils.entityDistanceToGround(this.entity, this);
    }

    public boolean isStandsOnEntity()
    {
        return this.standsOnEntity.get();
    }

    private boolean computeStandsOnEntity()
    {
        return EntityUtils.standsOnEntity(this.entity, this.aabb);
    }

    public long getFlags()
    {
        return this.flags.get();
    }

    private long computeFlags()
    {
        return BlockUtils.getFlags(this.entity.getWorld(), this.aabb);
    }

    // Metody dotyczące Location/IPosition. //

    @Override
    public Location toBukkit()
    {
        return this.location;
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
}
