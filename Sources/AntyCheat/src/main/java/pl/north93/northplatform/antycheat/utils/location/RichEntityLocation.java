package pl.north93.northplatform.antycheat.utils.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import org.diorite.commons.lazy.BooleanLazyValue;
import org.diorite.commons.lazy.DoubleLazyValue;
import org.diorite.commons.lazy.LongLazyValue;

import pl.north93.northplatform.antycheat.utils.AABB;
import pl.north93.northplatform.antycheat.utils.DistanceUtils;
import pl.north93.northplatform.antycheat.utils.EntityUtils;
import pl.north93.northplatform.antycheat.utils.block.BlockUtils;

public final class RichEntityLocation implements IPosition
{
    private final Entity   entity;
    private final Location location;
    private final AABB     aabb;

    /** Określa dystans dzielący entity i twardy grunt */
    private final DoubleLazyValue  distanceToGround;

    /** Określa czy entity stoi na innym entity */
    private final BooleanLazyValue standsOnEntity;

    /** Wszystkie flagi blogów obowiązujące bloki kolidująca z AABB */
    private final LongLazyValue    flags;

    /**
     * Tworzy nową instancję RichEntityLocation dla danego entity będącego w określonej lokalizacji.
     *
     * @param entity Entity dla którego tworzymy tą lokację.
     * @param location Lokacja w której znajduje się entity.
     */
    public RichEntityLocation(final Entity entity, final Location location)
    {
        this.entity = entity;
        this.location = location;
        this.aabb = EntityUtils.getAABBOfEntityInLocation(entity, this); // mozna przekazac bezpiecznie this bo location jest juz ustawione

        this.distanceToGround = new DoubleLazyValue(this::computeDistanceToGround);
        this.standsOnEntity = new BooleanLazyValue(this::computeStandsOnEntity);
        this.flags = new LongLazyValue(this::computeFlags);
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
     * Oblicza wektor wskazujący od tej lokalizacji do tej podanej w argumencie.
     *
     * @param position Lokalizacja na którą wskazuje wektor.
     * @return Wektor od tej lokalizacji do tej podanej w argumencie.
     */
    public Vector vectorToOther(final IPosition position)
    {
        return new Vector(position.getX() - this.getX(), position.getY() - this.getY(), position.getZ() - this.getZ());
    }

    public double distance(final IPosition other)
    {
        final double x = this.getX() - other.getX();
        final double y = this.getY() - other.getY();
        final double z = this.getZ() - other.getZ();

        return Math.sqrt(x * x + y * y + z * z);
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

    @Override
    public Vector getDirection()
    {
        return this.location.getDirection();
    }
}
