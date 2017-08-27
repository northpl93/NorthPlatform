package pl.arieals.api.minigame.shared.api;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public enum GamePhase
{
    /**
     * Wykonywane po utworzeniu areny i po zakonczeniu.
     * W przypadku areny z zewnetrznym lobby automatycznie przejdzie do LOBBY.
     * Przy zintegrowanym lobby bedzie czekal na wczytanie mapy.
     */
    INITIALISING,
    /**
     * Faza przed gra, jest przelaczana w {@link #STARTED} gdy jest wymagana ilosc graczy
     * i licznik dojdzie do zera.
     */
    LOBBY,
    /**
     * Tutaj odbywa sie wlasciwa gra. Plugin minigry jest odpowiedzialny za przelaczenie
     * areny do trybu {@link #POST_GAME} gdy gra sie zakonczy.
     * Gracze moga wchodzic do areny tylko w trybie lobby {@link LobbyMode#EXTERNAL}
     */
    STARTED,
    /**
     * Nastepuje wyswietlenie wynikow. Plugin jest odpowiedzialny za wywolanie
     * metody inicjujacej nowy cykl areny {@link LocalArena#prepareNewCycle()}
     */
    POST_GAME
}
