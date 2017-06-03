package pl.north93.zgame.skyblock.server.listeners.islandhost;

import org.bukkit.Material;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northspigot.event.blockchange.BlockChangeHandler;
import pl.north93.northspigot.event.blockchange.BlockChangedInfo;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.world.Island;

public class RankingHandler implements BlockChangeHandler
{
    @Inject
    private SkyBlockServer server;

    @Override
    public void handle(final BlockChangedInfo event)
    {
        final Island island = this.server.getServerManager().getIslandAt(event.getLocation());
        if (island == null)
        {
            return;
        }

        if (event.getNewMaterial() == Material.AIR)
        {
            island.getPoints().blockRemoved(event.getOldMaterial(), event.getOldData());
        }
        else if (event.getOldMaterial() == Material.AIR)
        {
            island.getPoints().blockAdded(event.getNewMaterial(), event.getNewData());
        }
        else
        {
            island.getPoints().blockRemoved(event.getOldMaterial(), event.getOldData());
            island.getPoints().blockAdded(event.getNewMaterial(), event.getNewData());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
