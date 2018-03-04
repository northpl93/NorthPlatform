package pl.arieals.minigame.goldhunter.listener;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.GameTeam;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class ChatListener implements AutoListener
{
    private final GoldHunter goldHunter;
    
    public ChatListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        if ( player == null || player.getArena() == null )
        {
            return;
        }
        
        GameTeam team = player.getTeam();
        boolean global = team == null;
        if ( event.getMessage().startsWith("!") )
        {
            global = true;
            event.setMessage(event.getMessage().substring(1));
        }

        if ( event.getMessage().trim().isEmpty() )
        {
            event.setCancelled(true);
        }
        
        if ( global )
        {
            event.setFormat("§6\u25A0 " + event.getFormat());
        }
        else
        {
            event.setFormat(team.getSecondaryTeamColor() + "\u25A0 " + event.getFormat());
        }
        
        if ( !global )
        {
            filterRecipients(team, event.getRecipients());
        }
    }
    
    private void filterRecipients(GameTeam team, Set<Player> recipients)
    {
        Iterator<Player> it = recipients.iterator();
        
        while ( it.hasNext() )
        {
            Player recipient = it.next();
            
            if ( recipient.hasPermission("gh.chat.viewall") )
            {
                continue;
            }
            
            if ( goldHunter.getPlayer(recipient).getTeam() != team )
            {
                it.remove();
            }
        }
    }
}
