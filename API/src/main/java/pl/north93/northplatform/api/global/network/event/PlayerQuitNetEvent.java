package pl.north93.northplatform.api.global.network.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.redis.event.INetEvent;
import pl.north93.northplatform.api.global.network.impl.players.OnlinePlayerImpl;

/**
 * Sieciowy event wywoływany w momencie gdy gracz wychodzi z sieci.
 * A dokładniej to gdy odłącza się od BungeeCorda.
 */
public class PlayerQuitNetEvent implements INetEvent
{
    private OnlinePlayerImpl player;

    public PlayerQuitNetEvent()
    {
    }

    public PlayerQuitNetEvent(final OnlinePlayerImpl player)
    {
        this.player = player;
    }

    /**
     * Zwraca obiekt gracza w stanie przed zapisaniem do bazy.
     * Mimo że to jest IOnlinePlayer to należy go traktować już jako offline.
     *
     * @return Niemodyfikowalny obiekt gracza.
     */
    public IPlayer getPlayer()
    {
        return this.player;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
