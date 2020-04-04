package pl.north93.northplatform.api.bukkit.player.event;

import java.util.Locale;

import org.bukkit.event.HandlerList;

import lombok.ToString;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

@ToString(of = {"newLocale"})
public class PlayerPlatformLocaleChangedEvent extends NorthPlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final Locale newLocale;

    public PlayerPlatformLocaleChangedEvent(final INorthPlayer who, final Locale newLocale)
    {
        super(who);
        this.newLocale = newLocale;
    }

    public Locale getNewLocale()
    {
        return this.newLocale;
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
