package pl.arieals.api.minigame.server.party;

public enum ClientResponse
{
    /**
     * Polecenie wykonane poprawnie.
     */
    OK,
    /**
     * Gracz musi być liderem grupy aby wykonać to polecenie.
     */
    NO_OWNER,
    /**
     * Nie znaleziono gracza o podanym nicku.
     */
    NO_PLAYER,
    /**
     * Gracz nie posiada żadnego zaproszenia, więc nie może zaakceptować.
     */
    NO_INVITE,
    /**
     * Gracz nie posiada party z którch
     */
    NO_PARTY,
    /**
     * Gracz już jest w party więc nie można mu wysłać zaproszenia, ani on nie może go przyjąć.
     */
    ALREADY_IN_PARTY,
    ERROR
}
