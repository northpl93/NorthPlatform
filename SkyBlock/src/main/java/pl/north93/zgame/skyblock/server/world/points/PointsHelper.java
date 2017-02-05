package pl.north93.zgame.skyblock.server.world.points;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.WorldManager;

public class PointsHelper
{
    private ApiCore        apiCore;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer skyBlockServer;
    private final Map<BlockData, Double> blockMap = new HashMap<>();
    private double defaultPrice;

    public double getBlockPrice(final Material material, final short data)
    {
        //skip non-solid blocks
        if(!material.isSolid())
        {
            return 0.0;
        }

        return this.blockMap.getOrDefault(new BlockData(material, data), this.defaultPrice);
    }

    public double getBlockPrice(final Block block)
    {
        return this.getBlockPrice(block.getType(), block.getData());
    }

    public void persistAll()
    {
        final IslandHostManager serverManager = this.skyBlockServer.getServerManager();
        for (final WorldManager worldManager : serverManager.getWorldManagers())
        {
            worldManager.getIslands().forEach(island -> island.getPoints().persist());
        }
    }

    public void recalculateAll()
    {
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final IslandHostManager serverManager = this.skyBlockServer.getServerManager();
            for (final WorldManager worldManager : serverManager.getWorldManagers())
            {
                worldManager.getIslands().forEach(island -> island.getPoints().recalculate());
            }
        });
    }

    public void load(final Map<String, Double> data)
    {
        for (final Map.Entry<String, Double> entry : data.entrySet())
        {
            final String blockName = entry.getKey();
            if (blockName.equalsIgnoreCase("default"))
            {
                this.defaultPrice = entry.getValue();
                return;
            }

            final int indexOf = blockName.indexOf(':');
            final Material material;
            final short materialData;
            if (indexOf == -1)
            {
                material = Material.valueOf(blockName);
                materialData = 0;
            }
            else
            {
                material = Material.valueOf(blockName.substring(0, indexOf));
                materialData = Short.parseShort(blockName.substring(indexOf + 1, blockName.length()));
            }
            this.blockMap.put(new BlockData(material, materialData), entry.getValue());
        }
    }

    /*default*/ void persist(final UUID islandId, final double points)
    {
        this.skyBlockServer.getIslandDao().modifyIsland(islandId, data ->
        {
            data.setPoints(points);
            if (data.getShowInRanking())
            {
                this.skyBlockServer.getIslandsRanking().setPoints(islandId, points);
            }
        });
    }

    private static class BlockData
    {
        private final Material material;
        private final short    data;

        private BlockData(final Material material, final short data)
        {
            this.material = material;
            this.data = data;
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

            final BlockData blockData = (BlockData) o;
            return this.data == blockData.data && this.material == blockData.material;
        }

        @Override
        public int hashCode()
        {
            int result = this.material.hashCode();
            result = 31 * result + this.data;
            return result;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("material", this.material).append("data", this.data).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("blockMap", this.blockMap).append("defaultPrice", this.defaultPrice).toString();
    }
}
