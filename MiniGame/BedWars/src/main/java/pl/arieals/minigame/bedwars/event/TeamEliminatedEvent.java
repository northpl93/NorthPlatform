package pl.arieals.minigame.bedwars.event;

import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;
import pl.arieals.minigame.bedwars.arena.Team;

public class TeamEliminatedEvent extends ArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final Team eliminatedTeam;

    public TeamEliminatedEvent(final LocalArena arena, final Team eliminatedTeam)
    {
        super(arena);
        this.eliminatedTeam = eliminatedTeam;
    }

    public Team getEliminatedTeam()
    {
        return this.eliminatedTeam;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("eliminatedTeam", this.eliminatedTeam).toString();
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
