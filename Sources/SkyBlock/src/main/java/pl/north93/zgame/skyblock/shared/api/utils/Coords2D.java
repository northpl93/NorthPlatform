package pl.north93.zgame.skyblock.shared.api.utils;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;

@SkipInjections
public final class Coords2D
{
    private Integer x;
    private Integer z;

    public Coords2D() // serialization
    {
    }

    public Coords2D(final int x, final int z)
    {
        this.x = x;
        this.z = z;
    }

    public int getX()
    {
        return this.x;
    }

    public int getZ()
    {
        return this.z;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final Coords2D coords2D = (Coords2D) o;

        return Objects.equals(this.x, coords2D.x) && Objects.equals(this.z, coords2D.z);
    }

    @Override
    public int hashCode()
    {
        int result = this.x;
        result = 31 * result + this.z;
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("x", this.x).append("z", this.z).toString();
    }
}
