package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import pl.north93.zgame.api.bukkit.utils.AutoListener;
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
        
        // TODO: obrazenia
    }
}
