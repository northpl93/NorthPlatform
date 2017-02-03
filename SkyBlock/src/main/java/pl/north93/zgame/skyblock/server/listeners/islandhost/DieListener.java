package pl.north93.zgame.skyblock.server.listeners.islandhost;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class DieListener implements Listener
{
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;

    @EventHandler
    public void changeDeadMessage(final PlayerDeathEvent event)
    {
        event.setDeathMessage(null);
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
