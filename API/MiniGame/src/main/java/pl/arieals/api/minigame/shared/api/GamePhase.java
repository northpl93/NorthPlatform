package pl.arieals.api.minigame.shared.api;

public enum GamePhase
{
    /**
     * Wykonywane po utworzeniu areny i po zakonczeniu.
     * W przypadku areny z zewnetrznym lobby automatycznie przejdzie do LOBBY.
     * Przy zintegrowanym lobby bedzie czekal na wcztyanie mapy.
     */
    INITIALISING,
    /**
     * Faza przed gra, jest przelaczana w STARTED gdy jest wymagana ilosc graczy i licznik
     * dojdzie do zera.
     */
    LOBBY,
    STARTED,
    POST_GAME
}
