package pl.north93.zgame.skyblock.shared.api.cfg;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.skyblock.shared.api.HomeLocation;

public class IslandConfig
{
    private String       name;
    private String       schematicName;
    private HomeLocation homeLocation;
    private Integer      generateAtHeight;
    private Integer      radius;

    public IslandConfig()
    {
    }

    public IslandConfig(final String name, final String schematicName, final HomeLocation homeLocation, final Integer generateAtHeight, final Integer radius)
    {
        this.name = name;
        this.schematicName = schematicName;
        this.homeLocation = homeLocation;
        this.generateAtHeight = generateAtHeight;
        this.radius = radius;
    }

    public String getName()
    {
        return this.name;
    }

    public String getSchematicName()
    {
        return this.schematicName;
    }

    public HomeLocation getHomeLocation()
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("schematicName", this.schematicName).append("homeLocation", this.homeLocation).append("generateAtHeight", this.generateAtHeight).append("radius", this.radius).toString();
    }
}
