package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public class PlayerQuitArenaEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final LocalArena arena;

    private String quitMessage;
    
    public PlayerQuitArenaEvent(final Player who, final LocalArena arena, final String quitMessage)
    {
        super(who);
        this.arena = arena;
        this.quitMessage = quitMessage;
    }

    public LocalArena getArena()
    {
        return this.arena;
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
