package pl.north93.zgame.api.economy;

import pl.north93.zgame.api.global.network.players.IPlayer;

public interface ITransaction extends AutoCloseable
{
    IPlayer getAssociatedPlayer();

    boolean has(double amount);

    void add(double amount);

    void remove(double amount);

    double getAmount();

    void setAmount(double newAmount);
}
