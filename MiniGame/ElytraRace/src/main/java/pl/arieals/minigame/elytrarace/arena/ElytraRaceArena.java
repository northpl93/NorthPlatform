package pl.arieals.minigame.elytrarace.arena;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;

public class ElytraRaceArena implements IArenaData
{
    private boolean isStarted;

    public boolean isStarted()
    {
        return this.isStarted;
    }

    public void setStarted(final boolean started)
    {
        this.isStarted = started;
    }
}
