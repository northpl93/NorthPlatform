package pl.north93.zgame.api.bungee.proxy.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

/**
 * Event wywoływany przed usunięciem danych gracza z Redisa.
 */
public class HandlePlayerProxyQuitEvent extends Event
{
    private final ProxiedPlayer        proxiedPlayer;
    private final Value<IOnlinePlayer> value;

    public HandlePlayerProxyQuitEvent(final ProxiedPlayer proxiedPlayer, final Value<IOnlinePlayer> value)
    {
        this.proxiedPlayer = proxiedPlayer;
        this.value = value;
    }

    public ProxiedPlayer getProxiedPlayer()
    {
        return this.proxiedPlayer;
    }

    public Value<IOnlinePlayer> getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("proxiedPlayer", this.proxiedPlayer).append("value", this.value).toString();
    }
}
