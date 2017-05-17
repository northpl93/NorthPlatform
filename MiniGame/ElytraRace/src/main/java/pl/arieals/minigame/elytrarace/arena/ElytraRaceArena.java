package pl.arieals.minigame.elytrarace.arena;

import static pl.arieals.minigame.elytrarace.ElytraRaceMode.RACE_MODE;
import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.minigame.elytrarace.ElytraRaceMode;
import pl.arieals.minigame.elytrarace.arena.meta.IFinishHandler;
import pl.arieals.minigame.elytrarace.arena.meta.RaceMetaHandler;
import pl.arieals.minigame.elytrarace.arena.meta.ScoreMetaHandler;
import pl.arieals.minigame.elytrarace.cfg.ArenaConfig;
import pl.arieals.minigame.elytrarace.cfg.ScoreGroup;

public class ElytraRaceArena implements IArenaData
{
    private final ArenaConfig    arenaConfig;
    private final ElytraRaceMode gameMode;
    private final IFinishHandler metaHandler;
    private       boolean        isStarted; // czy odliczanie do startu dobieglo konca

    public ElytraRaceArena(final ArenaConfig arenaConfig, final ElytraRaceMode gameMode)
    {
        this.arenaConfig = arenaConfig;
        this.gameMode = gameMode;
        this.metaHandler = gameMode == RACE_MODE ? new RaceMetaHandler() : new ScoreMetaHandler();
    }

    public ArenaConfig getArenaConfig()
    {
        return this.arenaConfig;
    }

    public ElytraRaceMode getGameMode()
    {
        return this.gameMode;
    }

    public boolean isStarted()
    {
        return this.isStarted;
    }

    public void setStarted(final boolean started)
    {
        this.isStarted = started;
    }

    public IFinishHandler getMetaHandler()
    {
        return this.metaHandler;
    }

    /**
     * Metoda pomocnicza do pobierania obiektu punktacji po nazwie.
     * @param name nazwa score group.
     * @return score group o podanej nazwie na tej mapie.
     */
    public ScoreGroup getScoreGroup(final String name)
    {
        return findInCollection(this.arenaConfig.getScoreGroups(), ScoreGroup::getName, name);
    }
}
