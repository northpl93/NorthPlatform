package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerDeathListener implements AutoListener
{
    @Inject
    private GoldHunter goldHunter;
    
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event)
    {
        event.setDeathMessage(null);
        event.setDroppedExp(0);
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        
        goldHunter.runTask(event.getEntity().spigot()::respawn);
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        GoldHunterPlayer player = MiniGameApi.getPlayerData(event.getPlayer(), GoldHunterPlayer.class);
        
        if ( player.isIngame() )
        {
            event.setRespawnLocation(player.respawn());
        }
        else
        {
            event.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
    }
}
