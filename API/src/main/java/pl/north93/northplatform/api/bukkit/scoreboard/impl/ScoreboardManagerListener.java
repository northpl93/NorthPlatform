package pl.north93.northplatform.api.bukkit.scoreboard.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class ScoreboardManagerListener implements AutoListener
{
    @Inject
    private ScoreboardManagerImpl scoreboardManager;
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void createPrivateScoreboardAtJoin(final PlayerJoinEvent event)
    {
        // domyslnie wszyscy gracze uzywaja tego samego globalnego scoreboardu.
        // ustawiamy natychmiastowo przy wejsciu prywatny scoreboard kazdemu graczowi
        final Player player = event.getPlayer();
        player.setScoreboard(player.getServer().getScoreboardManager().getNewScoreboard());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void deleteScoreboardOnExit(PlayerQuitEvent event)
    {
        scoreboardManager.removeScoreboard(event.getPlayer());
    }
}
