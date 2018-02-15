package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.util.Vector;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class AdditionalGameplayMechanicsListener implements AutoListener
{
    private final GoldHunter goldHunter;
    
    public AdditionalGameplayMechanicsListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler
    public void pushPlayerWhenCaughtByFishRod(PlayerFishEvent event)
    {
        if ( event.getState() != State.CAUGHT_ENTITY )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        Entity caught = event.getCaught();
        
        if ( player != null && caught != null )
        {
            Vector toCatcher = player.getPlayer().getLocation().subtract(caught.getLocation()).toVector();
            caught.setVelocity(toCatcher.normalize().setY(0).multiply(0.7454).setY(0.569));
        }
    }
}
