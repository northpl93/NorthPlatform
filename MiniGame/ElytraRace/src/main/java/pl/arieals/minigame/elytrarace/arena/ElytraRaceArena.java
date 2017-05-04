package pl.arieals.minigame.elytrarace.arena;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.minigame.elytrarace.cfg.ArenaConfig;

public class ElytraRaceArena implements IArenaData
{
    private final ArenaConfig arenaConfig;
    private       boolean     isStarted;

    public ElytraRaceArena(final ArenaConfig arenaConfig)
    {
        this.arenaConfig = arenaConfig;
    }

    public ArenaConfig getArenaConfig()
    {
        return this.arenaConfig;
    }

    public boolean isStarted()
    {
        return this.isStarted;
    }

    public void setStarted(final boolean started)
    {
        this.isStarted = started;
    }
}
