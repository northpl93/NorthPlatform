package pl.arieals.minigame.bedwars.event;

import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerArenaEvent;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;

public class PlayerRevivedEvent extends PlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final BedWarsPlayer bedWarsPlayer;

    public PlayerRevivedEvent(final LocalArena arena, final BedWarsPlayer bedWarsPlayer)
    {
        super(arena, bedWarsPlayer.getBukkitPlayer());
        this.bedWarsPlayer = bedWarsPlayer;
    }

    public BedWarsPlayer getBedWarsPlayer()
    {
        return this.bedWarsPlayer;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bedWarsPlayer", this.bedWarsPlayer).toString();
    }
}
