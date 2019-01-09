package pl.north93.zgame.skyblock.server.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WorldManagerList
{
    private final Map<String, WorldManager> byIslandType;
    private final Map<World, WorldManager>  byWorld;

    public WorldManagerList()
    {
        this.byIslandType = new HashMap<>(2);
        this.byWorld = new HashMap<>(2);
    }

    public void add(final WorldManager worldManager)
    {
        this.byIslandType.put(worldManager.getIslandConfig().getName(), worldManager);
        this.byWorld.put(worldManager.getWorld(), worldManager);
    }

    public WorldManager get(final String islandType)
    {
        return this.byIslandType.get(islandType);
    }

    public WorldManager get(final World world)
    {
        return this.byWorld.get(world);
    }

    public Collection<WorldManager> list()
    {
        return this.byIslandType.values();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
