package pl.north93.northplatform.api.minigame.shared.api;

/**
 * Przedstawia status gracza będącego na arenie minigry.
 */
public enum PlayerStatus
{
    /**
     * Gracz aktywnie biorący udział w grze.
     */
    PLAYING,
    /**
     * Gracz biorący udział w grze, ale aktualnie
     * jest w trybie spectatora (bo np. umarl).
     */
    PLAYING_SPECTATOR,
    /**
     * Gracz nie bioracy udzialu w grze, tylko ja
     * oglada.
     */
    SPECTATOR;

    /**
     * Sprawdza czy dany gracz bierze udzial w grze. <br>
     * Zwraca {@code true} dla {@link PlayerStatus#PLAYING} i {@link PlayerStatus#PLAYING_SPECTATOR}.
     *
     * @return czy gracz bierze udzial w grze.
     */
    public boolean isPlaying()
    {
        return this == PLAYING || this == PLAYING_SPECTATOR;
    }

    /**
     * Sprawdza czy dany gracz jest spectatorem. <br>
     * Zwraca {@code true} dla {@link PlayerStatus#PLAYING_SPECTATOR} i {@link PlayerStatus#SPECTATOR}.
     *
     * @return czy gracz jest spectatorem.
     */
    public boolean isSpectator()
    {
        return this == PLAYING_SPECTATOR || this == SPECTATOR;
    }
}
