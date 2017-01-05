package pl.north93.zgame.skyblock.api.cfg;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.skyblock.api.utils.Coords3D;

public class IslandConfig
{
    private String   name;
    private Coords3D homeLocation;
    private Integer  generateAtHeight;
    private Integer  radius;

    public IslandConfig()
    {
    }

    public IslandConfig(final String name, final Coords3D homeLocation, final Integer generateAtHeight, final Integer radius)
    {
        this.name = name;
        this.homeLocation = homeLocation;
        this.generateAtHeight = generateAtHeight;
        this.radius = radius;
    }

    public String getName()
    {
        return this.name;
    }

    public Coords3D getHomeLocation()
    {
        return this.homeLocation;
    }

    public Integer getGenerateAtHeight()
    {
        return this.generateAtHeight;
    }

    public Integer getRadius()
    {
        return this.radius;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("generateAtHeight", this.generateAtHeight).append("radius", this.radius).toString();
    }
}
