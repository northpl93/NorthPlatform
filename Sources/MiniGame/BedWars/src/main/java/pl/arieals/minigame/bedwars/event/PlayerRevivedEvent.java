package pl.arieals.minigame.bedwars.event;

import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;

public class PlayerRevivedEvent extends BedWarsPlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();

    public PlayerRevivedEvent(final LocalArena arena, final BedWarsPlayer bedWarsPlayer)
    {
        super(arena, bedWarsPlayer);
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
