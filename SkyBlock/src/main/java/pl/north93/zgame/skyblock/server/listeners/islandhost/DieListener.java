package pl.north93.zgame.skyblock.server.listeners.islandhost;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.world.Island;

public class DieListener implements Listener
{
    @Inject
    private SkyBlockServer server;

    @EventHandler
    public void dontDropItems(final PlayerDeathEvent event)
    {
        final Player entity = event.getEntity();
        final Island island = this.server.getServerManager().getIslandAt(entity.getLocation());
        if (! this.server.canAccess(entity, island) || entity.hasPermission("skyblock.death.keepinventory"))
        {
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setKeepInventory(true);
            event.setKeepLevel(true);
        }

        event.setDeathMessage(null); // remove death message
    }

    @EventHandler
    public void teleportToSpawn(final PlayerRespawnEvent event)
    {
        this.server.getServerManager().tpPlayerToSpawn(event.getPlayer());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
