package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;

public class ChatListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void appendTeamAndFilter(final AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            return;
        }

        if (playerData.isEliminated())
        {
            final Iterator<Player> recipients = event.getRecipients().iterator();
            while (recipients.hasNext())
            {
                final BedWarsPlayer recipientData = getPlayerData(recipients.next(), BedWarsPlayer.class);
                if (recipientData == null || !recipientData.isEliminated())
                {
                    recipients.remove();
                }
            }
        }
        else
        {
            event.setFormat(playerData.getTeam().getColor() + "■ " + event.getFormat());
        }
    }
}
