package pl.arieals.lobby.chest.opening;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.chest.loot.LootResult;

public class PresentOpeningResultsEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final IOpeningSession openingSession;
    private final LootResult result;

    public PresentOpeningResultsEvent(final Player who, final IOpeningSession openingSession, final LootResult result)
    {
        super(who);
        this.openingSession = openingSession;
        this.result = result;
    }

    public IOpeningSession getOpeningSession()
    {
        return this.openingSession;
    }

    public LootResult getResult()
    {
        return this.result;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("openingSession", this.openingSession).append("result", this.result).toString();
    }
}
