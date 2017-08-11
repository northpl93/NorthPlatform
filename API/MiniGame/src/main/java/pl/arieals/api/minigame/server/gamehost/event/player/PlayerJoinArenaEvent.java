package pl.arieals.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

/**
 * Event wywolywany gdy gracz wchodzi na arene.
 * Jest wywolywany takze gdy gracz ponownie wchodzi na
 * arene w trakcie gry, gdy nastapilo rozlaczenie.
 */
public class PlayerJoinArenaEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final boolean reconnected;
    private String joinMessage;

    public PlayerJoinArenaEvent(final Player who, final LocalArena arena, final boolean reconnected, final String joinMessage)
    {
        super(arena, who);
        this.reconnected = reconnected;
        this.joinMessage = joinMessage;
    }

    /**
     * Czy gracz juz byl wczesniej na arenie i czy laczy sie ponownie.
     * (obsluga mechanizmu ponownego dolaczenia do areny po wywaleniu z serwera itp.)
     * W fazie lobby tu bedzie zawsze false.
     *
     * @return czy gracz laczy sie do areny ponownie.
     */
    public boolean isReconnected()
    {
        return this.reconnected;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).append("reconnected", this.reconnected).append("joinMessage", this.joinMessage).toString();
    }
}
