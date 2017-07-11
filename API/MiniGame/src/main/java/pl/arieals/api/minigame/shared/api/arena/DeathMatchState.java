package pl.arieals.api.minigame.shared.api.arena;

public enum DeathMatchState
{
    NOT_STARTED,
    LOADING,
    STARTED;

    public boolean isStarted()
    {
        return this == STARTED;
    }

    public boolean isLoading()
    {
        return this == LOADING;
    }

    /**
     * Zwraca prawde jesli deathmatch startuje lub juz jest aktywny.
     * @return czy deathmatch sie wczytuje lub wystartowal.
     */
    public boolean isActive()
    {
        return this == LOADING || this == STARTED;
    }
}
