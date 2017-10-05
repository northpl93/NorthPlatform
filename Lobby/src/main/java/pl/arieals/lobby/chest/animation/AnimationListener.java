package pl.arieals.lobby.chest.animation;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class AnimationListener implements Listener
{
    private final ChestAnimationController animationController;

    public AnimationListener(final ChestAnimationController animationController)
    {
        this.animationController = animationController;
    }

    @EventHandler
    public void onChestInteract(final PlayerInteractAtEntityEvent event)
    {
        final Entity target = event.getRightClicked();
        this.handleChestClick(target, event);
    }

    @EventHandler
    public void onHitChest(final EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            this.handleChestClick(event.getEntity(), event);
        }
    }

    private void handleChestClick(final Entity entity, final Cancellable cancellable)
    {
        if (! (entity instanceof CraftArmorStand))
        {
            return;
        }

        final AnimationInstance instance = this.animationController.getInstanceByEntity(entity);
        if (instance == null)
        {
            return; // nie kliknieto animacji
        }

        instance.handleClick();
        cancellable.setCancelled(true);
    }
}
