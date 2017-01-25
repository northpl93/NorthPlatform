package pl.north93.zgame.api.economy;

import java.util.UUID;

public interface IEconomyManager
{
    ICurrency getCurrency(String name);

    ITransaction openTransaction(ICurrency currency, UUID playerId);

    ITransaction openTransaction(ICurrency currency, String playerName);
}
