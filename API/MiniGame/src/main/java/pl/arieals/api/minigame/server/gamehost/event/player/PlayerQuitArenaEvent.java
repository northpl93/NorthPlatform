package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public class PlayerQuitArenaEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private String quitMessage;
    
    public PlayerQuitArenaEvent(final Player who, final LocalArena arena, final String quitMessage)
    {
        super(arena, who);
        this.quitMessage = quitMessage;
    }

    public String getQuitMessage()
    {
        return this.quitMessage;
    }
    
    public void setQuitMessage(String quitMessage)
    {
        this.quitMessage = quitMessage;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).toString();
    }
}
