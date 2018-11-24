package pl.north93.northplatform.api.bungee.proxy.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Event;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.redis.observable.Value;

/**
 * Event wywoływany po wczytaniu danych gracza do Redisa.
 * Można tutaj dokonać dodatkowych sprawdzeń czy gracz może wejść do sieci.
 */
public class HandlePlayerProxyJoinEvent extends Event
{
    private final PendingConnection    connection;
    private final Value<IOnlinePlayer> value;
    private       BaseComponent        cancelReason;

    public HandlePlayerProxyJoinEvent(final PendingConnection connection, final Value<IOnlinePlayer> value)
    {
        this.connection = connection;
        this.value = value;
    }

    public PendingConnection getConnection()
    {
        return this.connection;
    }

    public Value<IOnlinePlayer> getValue()
    {
        return this.value;
    }

    public boolean isCancelled()
    {
        return this.cancelReason != null;
    }

    public BaseComponent getCancelReason()
    {
        return this.cancelReason;
    }

    public void setCancelled(final BaseComponent reason)
    {
        this.cancelReason = reason;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("connection", this.connection).append("value", this.value).append("cancelReason", this.cancelReason).toString();
    }
}
