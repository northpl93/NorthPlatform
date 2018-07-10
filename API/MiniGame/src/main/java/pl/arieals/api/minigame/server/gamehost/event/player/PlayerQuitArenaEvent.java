package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;

public class PlayerQuitArenaEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private boolean canReconnect;
    private String  quitMessage;
    
    public PlayerQuitArenaEvent(final INorthPlayer who, final LocalArena arena, final boolean canReconnect, final String quitMessage)
    {
        super(arena, who);
        this.canReconnect = canReconnect;
        this.quitMessage = quitMessage;
    }

    public boolean canReconnect()
    {
        return this.canReconnect;
    }

    public void disallowReconnect()
    {
        this.canReconnect = false;
    }

    public String getQuitMessage()
    {
        return this.quitMessage;
    }
    
    public void setQuitMessage(final String quitMessage)
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
