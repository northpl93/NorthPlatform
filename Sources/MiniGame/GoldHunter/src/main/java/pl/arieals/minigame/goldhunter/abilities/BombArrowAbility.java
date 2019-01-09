package pl.arieals.minigame.goldhunter.abilities;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;

import net.minecraft.server.v1_12_R1.Entity;

import pl.arieals.minigame.goldhunter.effect.BombArrowEffect;
import pl.arieals.minigame.goldhunter.entity.BombArrow;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public class BombArrowAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new BombArrowEffect());
        return true;
    }
    
    @EventHandler
    public void onDamageByExplosion(EntityDamageByEntityEvent event)
    {
        Entity entity = ((CraftEntity) event.getDamager()).getHandle();
        if ( !( entity instanceof BombArrow ) )
        {
            return;
        }
        
        if ( event.getCause() == DamageCause.PROJECTILE )
        {
            event.setDamage(0);
            return;
        }
        
        event.setDamage(ThreadLocalRandom.current().nextDouble(10, 15));
    }
    
    @EventHandler
    public void onBombArrowExplode(EntityExplodeEvent event)
    {
        Entity entity = ((CraftEntity) event.getEntity()).getHandle();
        if ( !( entity instanceof BombArrow ) )
        {
            return;
        }
        
        event.setYield(0.1569f);
    }
}
