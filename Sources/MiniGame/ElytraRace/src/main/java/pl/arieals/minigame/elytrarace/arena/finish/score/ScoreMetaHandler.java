package pl.arieals.minigame.elytrarace.arena.finish.score;

import static java.util.Comparator.comparing;


import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.arieals.minigame.elytrarace.arena.finish.ElytraWinReward;
import pl.arieals.minigame.elytrarace.arena.finish.IFinishHandler;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.reward.CurrencyReward;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.northplatform.api.minigame.shared.api.statistics.filter.BestRecordFilter;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.NumberUnit;

public class ScoreMetaHandler implements IFinishHandler
{
    @Inject @Messages("ElytraRace")
    private MessagesBox messages;
    @Inject
    private IStatisticsManager statisticsManager;
    private final Set<ScoreFinishInfo> points = new HashSet<>(); // uzywane w SCORE_MODE do wyswietlania wynikow

    @Override
    public void handle(final LocalArena arena, final INorthPlayer player, final ElytraRacePlayer playerData)
    {
        playerData.setFinished(true);

        final ElytraScorePlayer scoreData = playerData.asScorePlayer();

        arena.getChatManager().broadcast(
                this.messages,
                "score.finish.broadcast",
                player.getDisplayName(),
                scoreData.getPoints());

        final IStatistic<Long, NumberUnit> scoreStatistic = this.getScoreStatistic(arena);
        final IStatisticHolder statisticsHolder = this.statisticsManager.getPlayerHolder(player.getUniqueId());

        this.points.add(new ScoreFinishInfo(player.getUniqueId(), player.getDisplayName(), scoreData.getPoints()));

        final boolean isFinished = IFinishHandler.checkFinished(arena);

        final NumberUnit points = new NumberUnit((long) scoreData.getPoints()); // ilosc punktow gracza w NumberUnit
        this.statisticsManager.getRecord(scoreStatistic, new BestRecordFilter()).whenComplete((bestRecord, throwable) ->
        {
            statisticsHolder.record(scoreStatistic, points).whenComplete((record, throwable2) ->
            {
                final ScoreMessage scoreMessage = new ScoreMessage(this.getTop(), bestRecord, !isFinished);
                if (isFinished)
                {
                    for (final INorthPlayer playerInArena : arena.getPlayersManager().getPlayers())
                    {
                        scoreMessage.print(playerInArena);
                    }
                }
                else
                {
                    scoreMessage.print(player);
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

    private List<ScoreFinishInfo> getTop()
    {
        final Comparator<ScoreFinishInfo> comparator = comparing(ScoreFinishInfo::getPoints).reversed();
        return this.points.stream().sorted(comparator).collect(Collectors.toList());
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
            final ScoreMessage scoreMessage = new ScoreMessage(this.getTop(), result, false);
            for (final INorthPlayer playerInArena : arena.getPlayersManager().getPlayers())
            {
                scoreMessage.print(playerInArena);
            }
        });

        arena.endGame();
    }

    @Override
    public void gameEnd(final LocalArena arena)
    {
        final List<ScoreFinishInfo> top = this.getTop(); // pobiera topke graczy
        if (top.isEmpty())
        {
            // nikt nie ukonczyl areny, malo prawdopodobne, ale moze sie zdazyc
            // gdy arena zostanie wylaczona np. komenda
            return;
        }

        final ElytraRaceArena arenaData = arena.getArenaData();

        final Iterator<ScoreFinishInfo> iterator = top.iterator();
        for (int place = 0; iterator.hasNext(); place++)
        {
            final ScoreFinishInfo finishInfo = iterator.next();
            if (place == 0)
            {
                // pierwszy gracz z topki jest zwyciezca
                this.bumpWinsCount(finishInfo);
            }

            final int reward = ElytraWinReward.calculateReward(arenaData.getPlayers().size(), place);
            final Identity identity = Identity.create(finishInfo.getUuid(), null);

            arena.getRewards().addReward(identity, new CurrencyReward("place", "minigame", reward));
        }
    }

    /**
     * Podnosi o jeden statystyke przechowujaca liczbe zwyciestw na elytrze.
     * Potrzebne do scoreboardu na lobby
     */
    private void bumpWinsCount(final ScoreFinishInfo firstPlayer)
    {
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
