package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;

import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.northspigot.event.PlayerPressQEvent;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class AbilityUseShortcutListener implements AutoListener
{
    @EventHandler
    public void onPressQ(PlayerPressQEvent event)
    {
        event.setCancelled(true);
        
        GoldHunterPlayer player = MiniGameApi.getPlayerData(event.getPlayer(), GoldHunterPlayer.class);
        player.getAbilityTracker().useAbility();
    }
}
