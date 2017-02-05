package pl.north93.zgame.api.economy;

import pl.north93.zgame.api.global.network.players.IPlayer;

public interface ITransaction extends AutoCloseable
{
    IPlayer getAssociatedPlayer();

    boolean has(double amount);

    /**
     * Dodaje wartość do konta.
     *
     * @param amount ilość pieniędzy.
     * @return ilość pieniędzy PRZED dodaniem.
     */
    double add(double amount);

    /**
     * Usuwa wartość z konta.
     *
     * @param amount ilość pieniędzy.
     * @return ilość pieniędzy PRZED odjęcien.
     */
    double remove(double amount);

    double getAmount();

    void setAmount(double newAmount);
}
