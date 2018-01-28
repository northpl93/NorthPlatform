package pl.north93.zgame.api.global.network.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.impl.OnlinePlayerImpl;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.redis.event.INetEvent;

/**
 * Event wywoływany gdy gracz wchodzi do sieci.
 */
public class PlayerJoinNetEvent implements INetEvent
{
    private OnlinePlayerImpl player;

    public PlayerJoinNetEvent()
    {
    }

    public PlayerJoinNetEvent(final OnlinePlayerImpl player)
    {
        this.player = player;
    }

    /**
     * Zwraca obiekt gracza w stanie po załadowaniu z bazy.
     * Nie można go modyfikować, w tym celu należy otworzyć transakcję w {@link IPlayersManager}.
     *
     * @return Niemodyfikowalny obiekt gracza w stanie po załadowaniu z bazy.
     */
    public IOnlinePlayer getPlayer()
    {
        return this.player;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
