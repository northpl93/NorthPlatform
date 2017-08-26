package pl.arieals.minigame.goldhunter.abilities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import pl.arieals.minigame.goldhunter.AbilityHandler;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;

public class ShieldAttackAbility implements AbilityHandler
{
    private static final double ABILITY_RADIUS = 3;
    private static final double DAMAGE = 7;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        for ( Entity e : player.getPlayer().getNearbyEntities(ABILITY_RADIUS, ABILITY_RADIUS, ABILITY_RADIUS) )
        {
            if ( !( e instanceof LivingEntity ) )
            {
                continue;
            }
            
            LivingEntity le = (LivingEntity) e;
            le.damage(DAMAGE, player.getPlayer());
            
            Vector entityPos = le.getLocation().toVector();
            Vector playerPos = player.getPlayer().getLocation().toVector();
            
            Vector velocity = entityPos.subtract(playerPos).setY(0).normalize().multiply(3).setY(2);
            le.setVelocity(velocity);
        }
        
        // TODO: play particles
        
        return true;
    }
}
