package pl.north93.zgame.api.economy;

import java.util.UUID;

import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.players.Identity;

public interface IEconomyManager
{
    ICurrency getCurrency(String name);

    ICurrencyRanking getRanking(ICurrency currency);

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
