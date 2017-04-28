package pl.arieals.api.minigame.shared.api;

import javax.vecmath.Point3i;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

public class GameMap
{
    @CfgComment("Nazwa wyświetlana mapy")
    private String              displayName;
    @CfgComment("Nazwa katalogu przechowującego mapę")
    private String              directory;
    private Point3i             arenaRegion1;
    private Point3i             arenaRegion2;
    @CfgComment("Dodatkowe ustawienia")
    private Map<String, String> properties;

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getDirectory()
    {
        return this.directory;
    }

    public Point3i getArenaRegion1()
    {
        return this.arenaRegion1;
    }

    public Point3i getArenaRegion2()
    {
        return this.arenaRegion2;
    }

    public Map<String, String> getProperties()
    {
        return this.properties;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("displayName", this.displayName).append("directory", this.directory).append("arenaRegion1", this.arenaRegion1).append("arenaRegion2", this.arenaRegion2).append("properties", this.properties).toString();
    }
}
