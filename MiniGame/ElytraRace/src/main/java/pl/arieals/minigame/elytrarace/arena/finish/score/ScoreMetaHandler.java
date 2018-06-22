package pl.arieals.minigame.elytrarace.arena.finish.score;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.arieals.api.minigame.shared.api.statistics.filter.BestRecordFilter;
import pl.arieals.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.arieals.minigame.elytrarace.arena.finish.IFinishHandler;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.lang.MapCollector;

public class ScoreMetaHandler implements IFinishHandler
{
    @Inject @Messages("ElytraRace")
    private MessagesBox        messages;
    @Inject
    private IStatisticsManager statisticsManager;
    private IRecord            currentGlobalRecord;
    private final Map<ScoreFinishInfo, Integer> points = new HashMap<>(); // uzywane w SCORE_MODE do wyswietlania wynikow

    @Override
    public void handle(final LocalArena arena, final Player player, final ElytraRacePlayer playerData)
    {
        playerData.setFinished(true);

        final ElytraScorePlayer scoreData = getPlayerData(player, ElytraScorePlayer.class);

        arena.getChatManager().broadcast(
                this.messages,
                "score.finish.broadcast",
                player.getDisplayName(),
                scoreData.getPoints());

        final IStatistic<NumberUnit> scoreStatistic = this.getScoreStatistic(arena);
        final IStatisticHolder statisticsHolder = this.statisticsManager.getPlayerHolder(player.getUniqueId());

        this.points.put(new ScoreFinishInfo(player.getUniqueId(), player.getDisplayName()), scoreData.getPoints());

        final boolean isFinished = IFinishHandler.checkFinished(arena);

        final NumberUnit points = new NumberUnit((long) scoreData.getPoints()); // ilosc punktow gracza w NumberUnit
        this.statisticsManager.getRecord(scoreStatistic, new BestRecordFilter()).whenComplete((bestRecord, throwable) ->
        {
            statisticsHolder.record(scoreStatistic, points).whenComplete((record, throwable2) ->
            {
                final ScoreMessage raceMessage = new ScoreMessage(this.getTop(), bestRecord, !isFinished);
                if (isFinished)
                {
                    for (final Player playerInArena : arena.getPlayersManager().getPlayers())
                    {
                        raceMessage.print(playerInArena);
                    }
                }
                else
                {
                    raceMessage.print(player);
                }
            });
        });

        // podbijamy statystyke zliczajaca zdobyte punkty
        final HigherNumberBetterStatistic totalScorePointsStat = new HigherNumberBetterStatistic("elytra/totalScorePoints");
        statisticsHolder.increment(totalScorePointsStat, points);

        if (isFinished)
        {
            arena.endGame();
        }
    }

    private Map<ScoreFinishInfo, Integer> getTop()
    {
        final Comparator<Map.Entry<ScoreFinishInfo, Integer>> reversed = Map.Entry.<ScoreFinishInfo, Integer>comparingByValue().reversed();
        return this.points.entrySet().stream().sorted(reversed).collect(MapCollector.toMap());
    }

    @Override
    public void playerQuit(final LocalArena arena, final Player player)
    {
        if (! IFinishHandler.checkFinished(arena))
        {
            return;
        }

        final HigherNumberBetterStatistic scoreStatistic = this.getScoreStatistic(arena);
        this.statisticsManager.getRecord(scoreStatistic, new BestRecordFilter()).whenComplete((result, throwable) ->
        {
            final ScoreMessage raceMessage = new ScoreMessage(this.getTop(), result, false);
            for (final Player playerInArena : arena.getPlayersManager().getPlayers())
            {
                raceMessage.print(playerInArena);
            }
        });

        arena.endGame();
    }

    @Override
    public void gameEnd(final LocalArena arena)
    {
        this.bumpWinsCount();
    }

    /**
     * Podnosi o jeden statystyke przechowujaca liczbe zwyciestw na elytrze.
     * Potrzebne do scoreboardu na lobby
     */
    private void bumpWinsCount()
    {
        final Map<ScoreFinishInfo, Integer> top = this.getTop(); // pobiera topke graczy
        if (top.isEmpty())
        {
            // nikt nie ukonczyl areny, malo prawdopodobne, ale moze sie zdazyc
            // gdy arena zostanie wylaczona np. komenda
            return;
        }

        final ScoreFinishInfo firstPlayer = top.keySet().iterator().next(); // pobiera pierwszego gracza z topki
        final IStatisticHolder holder = this.statisticsManager.getPlayerHolder(firstPlayer.getUuid());

        final HigherNumberBetterStatistic totalElytraWins = new HigherNumberBetterStatistic("elytra/totalWins");
        holder.increment(totalElytraWins, new NumberUnit(1L));
    }

    private HigherNumberBetterStatistic getScoreStatistic(final LocalArena arena)
    {
        return new HigherNumberBetterStatistic("elytra/score/" + arena.getWorld().getCurrentMapTemplate().getName());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("points", this.points).toString();
    }
}
