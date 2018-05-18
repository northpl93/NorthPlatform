package pl.north93.zgame.api.bukkit.scoreboard.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class ScoreboardManagerListener implements AutoListener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void createPrivateScoreboardAtJoin(final PlayerJoinEvent event)
    {
        // domyslnie wszyscy gracze uzywaja tego samego globalnego scoreboardu.
        // ustawiamy natychmiastowo przy wejsciu prywatny scoreboard kazdemu graczowi
        final Player player = event.getPlayer();
        player.setScoreboard(player.getServer().getScoreboardManager().getNewScoreboard());
    }
}
