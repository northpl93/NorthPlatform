package pl.north93.zgame.api.economy;

import pl.north93.zgame.api.global.network.players.IPlayer;

/**
 * Listener wywoływany podczas operacji na transakcji na lokalnym serwerze.
 * Informacje z zdalnych serwerów NIE są nasłuchiwane.
 */
public interface ITransactionListener
{
    void amountUpdated(IPlayer player, ICurrency currency, double newAmount);
}
