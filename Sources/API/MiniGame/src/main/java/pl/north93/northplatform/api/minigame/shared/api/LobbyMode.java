package pl.north93.northplatform.api.minigame.shared.api;

/**
 * Reprezentuje tryb dzialania lobby na danej minigrze.
 */
public enum LobbyMode
{
    /**
     * Znajduje sie na mapie 0
     * Serwer pilnuje zeby gracze sie nie widzieli
     *
     * Mozliwe jest glosowanie na mape
     */
    EXTERNAL,
    /**
     * Znajduje sie na mapie z gra
     */
    INTEGRATED
}
