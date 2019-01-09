package pl.north93.northplatform.api.minigame.server.gamehost.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event wywoływany gdy gracz bez powiązanej areny wchodzi
 * na serwer hostujący grę.
 */
public class PlayerJoinWithoutArenaEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();

    public PlayerJoinWithoutArenaEvent(final Player who)
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
