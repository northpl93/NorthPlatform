package pl.arieals.minigame.goldhunter.abilities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ShieldAttackAbility implements AbilityHandler
{
    private static final double ABILITY_RADIUS = 3;
    private static final double DAMAGE = 7;
    
    @Inject
    private static GoldHunter goldHunter;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        for ( Entity e : player.getPlayer().getNearbyEntities(ABILITY_RADIUS, ABILITY_RADIUS, ABILITY_RADIUS) )
        {
            if ( !( e instanceof LivingEntity ) )
            {
                continue;
            }
            
            LivingEntity entity = (LivingEntity) e;
            
            if ( entity instanceof Player )
            {
                GoldHunterPlayer other = goldHunter.getPlayer((Player) entity);
                if ( other != null && other.getTeam() == player.getTeam() )
                {
                    continue;
                }
            }
            
            Vector entityPos = entity.getLocation().toVector();
            Vector playerPos = player.getPlayer().getLocation().toVector();
            
            Vector toTarget = entityPos.clone().subtract(playerPos).setY(0).normalize();
            Vector dir = player.getPlayer().getLocation().getDirection().setY(0).normalize();
            
            if ( Math.abs(Math.toDegrees(toTarget.angle(dir))) > 30 )
            {
                continue;
            }
            
            double toDamage = Math.min(Math.max(entity.getHealth() - 1, 0), DAMAGE);
            entity.damage(toDamage, player.getPlayer());
            
            Vector velocity = toTarget.multiply(0.819693).setY(0.56969);
            entity.setVelocity(velocity);
        }
        
        // TODO: play particles
        
        return true;
    }
}
