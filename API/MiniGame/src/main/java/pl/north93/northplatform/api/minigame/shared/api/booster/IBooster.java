package pl.north93.northplatform.api.minigame.shared.api.booster;

import pl.north93.northplatform.api.global.network.players.IPlayer;

public interface IBooster
{
    /**
     * @return Unikalny ciag znaków reprezentujacy ten booster.
     */
    String getId();

    /**
     * Zawraca czas wygasniecia boostera w unixowym timestampie.
     * Jesli nigdy nie wygasa to zwraca -1.
     *
     * @param player Gracz dla którego pobieramy wartosc.
     * @return Czas wygasniecia boostera w unix timestamp.
     */
    default long getExpiration(final IPlayer player)
    {
        return -1;
    }

    default boolean isBoosterValid(final IPlayer player)
    {
        final long expiration = this.getExpiration(player);
        return expiration == - 1 || expiration > System.currentTimeMillis();
    }

    /**
     * Zwraca wartosc która zostanie dodana do ogólnego mnoznika gracza.
     *
     * @param player Gracz dla którego pobieramy wartosc.
     * @return Wartosc która zostanie dodana do mnoznika gracza.
     */
    double getMultiplier(IPlayer player);
}
