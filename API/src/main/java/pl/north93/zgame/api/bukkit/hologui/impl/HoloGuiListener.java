package pl.north93.zgame.api.bukkit.hologui.impl;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HoloGuiListener implements Listener
{
    private final HoloGuiManagerImpl holoGuiManager;

    public HoloGuiListener(final HoloGuiManagerImpl holoGuiManager)
    {
        this.holoGuiManager = holoGuiManager;
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        // zamknie gui jesli gracz jakies posiada
        this.holoGuiManager.closeGui(event.getPlayer());
    }

    /*@EventHandler
    public void onMove(final PlayerMoveEvent event)
    {
        final HoloContextImpl playerContext = this.holoGuiManager.getPlayerContext(event.getPlayer());
        if (playerContext != null)
        {
            playerContext.setCenter(event.getTo());
        }
    }*/

    @EventHandler
    public void onEntityInteract(final PlayerInteractAtEntityEvent event)
    {
        final Entity target = event.getRightClicked();
        this.handleGuiClick(event.getPlayer(), target, event);
    }

    @EventHandler
    public void playerHitEntity(final EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            final Player player = (Player) event.getDamager();
            this.handleGuiClick(player, event.getEntity(), event);
        }
    }

    private void handleGuiClick(final Player player, final Entity entity, final Cancellable cancellable)
    {
        if (! (entity instanceof CraftArmorStand))
        {
            return;
        }

        final HoloContextImpl playerContext = this.holoGuiManager.getPlayerContext(player);
        if (playerContext == null)
        {
            return;
        }

        playerContext.handleClick(entity);
        cancellable.setCancelled(true);
    }
}
