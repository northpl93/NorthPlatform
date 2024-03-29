package pl.north93.northplatform.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.gui.JoinTeamGui;
import pl.north93.northplatform.minigame.goldhunter.gui.SelectClassGui;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northspigot.event.PlayerPressQEvent;
import pl.north93.northplatform.api.bukkit.server.AutoListener;

public class KeyShortcutListener implements AutoListener
{
    private final GoldHunter goldHunter;
    
    public KeyShortcutListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressQ(PlayerPressQEvent event)
    {
        event.setCancelled(true);
        
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        if ( player.isIngame() )
        {
            player.getAbilityTracker().useAbility();
        }
        else
        {
            new JoinTeamGui(player).open();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressF(PlayerSwapHandItemsEvent event)
    {
        event.setCancelled(true);
        
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        new SelectClassGui(player).open();
    }
}
