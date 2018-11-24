package pl.north93.northplatform.api.minigame.shared.api.arena.reconnect;

import pl.north93.northplatform.api.global.network.players.Identity;

public interface IReconnectManager
{
    ReconnectTicket getReconnectTicket(Identity player);

    void updateReconnectTicket(Identity player, ReconnectTicket newTicket);
}
