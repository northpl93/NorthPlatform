package pl.north93.zgame.api.economy;

import java.util.UUID;

import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.Identity;

public interface IEconomyManager
{
    ICurrency getCurrency(String name);

    ICurrencyRanking getRanking(ICurrency currency);

    /**
     * Umozliwia szybkie sprawdzanie aktualnej ilosci posiadanej
     * przez gracza waluty.
     *
     * Ten accessor jest niebezpieczny poniewaz nie otwiera transakcji.
     * Wartosci zwracane przez niego powinny byc uzywane tylko w
     * celach informacyjnych.
     *
     * @param currency Waluta dla ktorej tworzymy accessor.
     * @param identity Gracz ktoremu tworzymy accessor.
     * @return Nietransakcyjny accessor read-only.
     */
    IAccountAccessor getUnsafeAccessor(ICurrency currency, Identity identity);

    ITransaction openTransaction(ICurrency currency, Identity identity) throws PlayerNotFoundException;

    default ITransaction openTransaction(ICurrency currency, UUID playerId) throws PlayerNotFoundException
    {
        return this.openTransaction(currency, Identity.create(playerId, null));
    }

    default ITransaction openTransaction(ICurrency currency, String playerName) throws PlayerNotFoundException
    {
        return this.openTransaction(currency, Identity.create(null, playerName));
    }
}
