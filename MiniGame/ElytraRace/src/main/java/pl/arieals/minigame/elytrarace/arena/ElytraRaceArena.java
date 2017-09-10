package pl.arieals.minigame.elytrarace.arena;

import static pl.arieals.minigame.elytrarace.ElytraRaceMode.RACE_MODE;
import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.minigame.elytrarace.ElytraRaceMode;
import pl.arieals.minigame.elytrarace.arena.finish.IFinishHandler;
import pl.arieals.minigame.elytrarace.arena.finish.race.RaceMetaHandler;
import pl.arieals.minigame.elytrarace.arena.finish.score.ScoreMetaHandler;
import pl.arieals.minigame.elytrarace.cfg.ArenaConfig;
import pl.arieals.minigame.elytrarace.cfg.Checkpoint;
import pl.arieals.minigame.elytrarace.cfg.Score;
import pl.arieals.minigame.elytrarace.cfg.ScoreGroup;

public class ElytraRaceArena implements IArenaData
{
    private final ArenaConfig                 arenaConfig;
    private final ElytraRaceMode              gameMode;
    private final IFinishHandler              metaHandler;
    private final Map<Score, ScoreController> scoreControllers; // w trybie score mapuje score do jego kontrolera
    private       boolean                     isStarted; // czy odliczanie do startu dobieglo konca

    public ElytraRaceArena(final ArenaConfig arenaConfig, final ElytraRaceMode gameMode)
    {
        this.arenaConfig = arenaConfig;
        this.gameMode = gameMode;
        this.metaHandler = gameMode == RACE_MODE ? new RaceMetaHandler() : new ScoreMetaHandler();
        this.scoreControllers = new HashMap<>();
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

    public Map<Score, ScoreController> getScoreControllers()
    {
        return this.scoreControllers;
    }

    public ScoreController getScoreController(final Score score)
    {
        return this.scoreControllers.get(score);
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

    public int getMaxCheckpoints() // ilosc checkpointow, uzywane przy wyswietlaniu rzeczy typu: 2/7 checkpointy zaliczone.
    {
        return this.arenaConfig.getCheckpoints().stream().mapToInt(Checkpoint::getNumber).max().orElse(0);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaConfig", this.arenaConfig).append("gameMode", this.gameMode).append("isStarted", this.isStarted).toString();
    }
}
