package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public class PlayerJoinArenaEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final LocalArena arena;
    
    private String joinMessage;

    public PlayerJoinArenaEvent(final Player who, final LocalArena arena, final String joinMessage)
    {
        super(who);
        this.arena = arena;
    }

    public LocalArena getArena()
    {
        return this.arena;
    }

    public String getJoinMessage()
    {
        return this.joinMessage;
    }
    
    public void setJoinMessage(String joinMessage)
    {
        this.joinMessage = joinMessage;
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
