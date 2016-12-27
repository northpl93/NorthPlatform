package pl.north93.zgame.skyblock.api.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;

@SkipInjections
public class Coords3D
{
    private Integer x;
    private Integer y;
    private Integer z;

    public Coords3D()
    {
    }

    public Coords3D(final Integer x, final Integer y, final Integer z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Integer getX()
    {
        return this.x;
    }

    public Integer getY()
    {
        return this.y;
    }

    public Integer getZ()
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

        final Coords3D coords3D = (Coords3D) o;

        return this.x.equals(coords3D.x) && this.y.equals(coords3D.y) && this.z.equals(coords3D.z);
    }

    @Override
    public int hashCode()
    {
        int result = this.x.hashCode();
        result = 31 * result + this.y.hashCode();
        result = 31 * result + this.z.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("x", this.x).append("y", this.y).append("z", this.z).toString();
    }
}
