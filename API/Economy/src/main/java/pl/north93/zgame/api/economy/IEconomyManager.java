package pl.north93.zgame.api.economy;

import java.util.UUID;

import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;

public interface IEconomyManager
{
    ICurrency getCurrency(String name);

    ICurrencyRanking getRanking(ICurrency currency);

    ITransaction openTransaction(ICurrency currency, UUID playerId) throws PlayerNotFoundException;

    ITransaction openTransaction(ICurrency currency, String playerName) throws PlayerNotFoundException;
}
