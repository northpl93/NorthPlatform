package pl.arieals.minigame.goldhunter;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class GoldHunterPlayer
{
    private final Player player;
    private final GoldHunterArena arena;
    
    private IScoreboardContext scoreboardContext;
    
    @Inject
    @Messages("GoldHunter")
    private MessagesBox messages;
    
    public GoldHunterPlayer(Player player, GoldHunterArena arena)
    {
        this.player = player;
        this.arena = arena;
    }
    
    public GoldHunterArena getArena()
    {
        return arena;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    public IScoreboardContext getScoreboardContext()
    {
        return scoreboardContext;
    }
    
    public void setScoreboardContext(IScoreboardContext scoreboardContext)
    {
        this.scoreboardContext = scoreboardContext;
    }
    
    public void teleportToLobby()
    {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
    }
    
    public String getMessage(String msgKey, String... args)
    {
        return messages.getMessage(player.spigot().getLocale(), msgKey, args);
    }
    
    public String[] getMessageLines(String msgKey, String... args)
    {
        return messages.getMessage(player.spigot().getLocale(), msgKey, args).split("/n");
    }
    
    public void sendMessage(String msgKey, String... args)
    {
        player.sendMessage(getMessageLines(msgKey, args));
    }
    
    @Override
    public String toString()
    {
        return player.getName();
    }

 
}
