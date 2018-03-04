package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.gui.SelectClassGui;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northspigot.event.PlayerPressQEvent;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

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
        
        GoldHunterPlayer player = MiniGameApi.getPlayerData(event.getPlayer(), GoldHunterPlayer.class);
        player.getAbilityTracker().useAbility();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressF(PlayerSwapHandItemsEvent event)
    {
        event.setCancelled(true);
        
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        new SelectClassGui(player).open();
    }
}