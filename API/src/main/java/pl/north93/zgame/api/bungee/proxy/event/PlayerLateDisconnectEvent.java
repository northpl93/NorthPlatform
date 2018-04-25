package pl.north93.zgame.api.bungee.proxy.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Event;

/**
 * Bardziej poprawna wersja eventu {@link PlayerDisconnectEvent}.
 * <p>
 * Wywołuje się podczas wychodzenia gracza z proxy,
 * ale PO rozłączeniu z serwerem Minecrafta.
 */
public class PlayerLateDisconnectEvent extends Event
{
    private final ProxiedPlayer proxiedPlayer;

    public PlayerLateDisconnectEvent(final ProxiedPlayer proxiedPlayer)
    {
        this.proxiedPlayer = proxiedPlayer;
    }

    public ProxiedPlayer getPlayer()
    {
        return this.proxiedPlayer;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("proxiedPlayer", this.proxiedPlayer).toString();
    }
}
