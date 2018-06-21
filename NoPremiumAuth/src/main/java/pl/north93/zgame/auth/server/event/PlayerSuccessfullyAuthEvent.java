package pl.north93.zgame.auth.server.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event wywoływany gdy gracz no-premium zaloguje się lub zarejestruje.
 */
public class PlayerSuccessfullyAuthEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();

    public PlayerSuccessfullyAuthEvent(final Player who)
    {
        super(who);
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
}
