package pl.north93.zgame.api.economy;

import pl.north93.zgame.api.global.network.players.IPlayer;

public interface ITransaction extends AutoCloseable
{
    IPlayer getAssociatedPlayer();

    int getAmount();

    void setAmount(int newAmount);
}
