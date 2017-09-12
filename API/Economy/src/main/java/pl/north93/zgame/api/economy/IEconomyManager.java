package pl.north93.zgame.api.economy;

import java.util.UUID;

import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.Identity;

public interface IEconomyManager
{
    ICurrency getCurrency(String name);

    ICurrencyRanking getRanking(ICurrency currency);

    /**
     * Umozliwia szybkie pobranie aktualnej ilosci posiadanej
     * waluty przez gracza. Nie uruchamia transakcji.
     *
     * Ta wartosc powinna byc tylko informacyjna, przy
     * przetwarzaniu zakupu nalezy uzyc transakcji.
     *
     * @param currency Waluta ktorej ilosc sprawdzamy.
     * @param identity Gracz ktorego sprawdzamy.
     * @return Aktualna ilosc waluty.
     */
    double getAmount(ICurrency currency, Identity identity);

    ITransaction openTransaction(ICurrency currency, Identity identity) throws PlayerNotFoundException;

    default ITransaction openTransaction(ICurrency currency, UUID playerId) throws PlayerNotFoundException
    {
        return this.openTransaction(currency, Identity.create(playerId, null, null));
    }

    default ITransaction openTransaction(ICurrency currency, String playerName) throws PlayerNotFoundException
    {
        return this.openTransaction(currency, Identity.create(null, playerName, null));
    }
}
