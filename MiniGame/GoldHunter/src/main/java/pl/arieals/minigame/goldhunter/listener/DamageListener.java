package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.bukkit.utils.itemstack.MaterialUtils;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DamageListener implements AutoListener
{
    @Inject
    private GoldHunter goldHunter;
    
    public void onPlayerDamage(EntityDamageEvent event)
    {
        if ( !( event.getEntity() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) event.getEntity());
        
        if ( event.getCause() == DamageCause.FALL && player.getNoFallDamageTicks() > 0 )
        {
            event.setCancelled(true);
            player.setNoFallDamageTicks(0);
            return;
        }
    }
    
    @EventHandler
    public void onDamageByPlayer(EntityDamageByEntityEvent event)
    {
        if ( !( event.getDamager() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer damager = goldHunter.getPlayer((Player) event.getDamager());
        if ( damager == null )
        {
            return;
        }
        
        if ( !MaterialUtils.isSword(damager.getPlayer().getInventory().getItemInMainHand().getType()) )
        {
            event.setDamage(1);
        }
        
        if ( !( event.getEntity() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer damaged = goldHunter.getPlayer((Player) event.getEntity());
        if ( damaged == null || damager.getTeam().opositeTeam() != damaged.getTeam() )
        {
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }
        
        damaged.setLastDamager(damager);
    }
    
    @EventHandler
    public void onDamagedByProjectile(EntityDamageByEntityEvent event)
    {
        if ( !( event.getDamager() instanceof Projectile ) )
        {
            return;
        }
        
        Projectile projectile = (Projectile) event.getDamager();
        
        if ( !( projectile.getShooter() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer damager = goldHunter.getPlayer((Player) projectile.getShooter());
        if ( damager == null )
        {
            return;
        }
        
        if ( !( event.getEntity() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer damaged = goldHunter.getPlayer((Player) event.getEntity());
        if ( damaged == null || damager.getTeam().opositeTeam() != damaged.getTeam() )
        {
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }
        
        damaged.setLastDamager(damager);
    }
}
