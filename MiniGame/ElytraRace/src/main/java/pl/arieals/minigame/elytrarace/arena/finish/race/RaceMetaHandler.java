package pl.arieals.minigame.elytrarace.arena.finish.race;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.arieals.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.type.LongerTimeBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.type.ShorterTimeBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.unit.DurationUnit;
import pl.arieals.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.finish.IFinishHandler;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class RaceMetaHandler implements IFinishHandler
{
    @Inject @Messages("ElytraRace")
    private MessagesBox          messages;
    @Inject
    private IStatisticsManager   statisticsManager;
    private List<RaceFinishInfo> finishInfo = new LinkedList<>();
    private int                  place; // uzywane w RACE_MODE do okreslania miejsca gracza

    @Override
    public void handle(final LocalArena arena, final Player player, final ElytraRacePlayer playerData)
    {
        playerData.setFinished(true);

        final int playerPlace = this.place + 1;
        this.place = playerPlace;

        final long playerTime = arena.getTimer().getCurrentTime(TimeUnit.MILLISECONDS);
        this.finishInfo.add(new RaceFinishInfo(player.getUniqueId(), player.getDisplayName(), playerTime, playerPlace));

        if (playerPlace == 1)
        {
            arena.getPlayersManager().broadcast(
                    this.messages,
                    "race.finish.broadcast_first",
                    player.getDisplayName(),
                    arena.getTimer().humanReadableTimeAfterStart());
        }
        else
        {
            arena.getPlayersManager().broadcast(
                    this.messages,
                    "race.finish.broadcast",
                    player.getDisplayName(),
                    playerPlace,
                    arena.getTimer().humanReadableTimeAfterStart());
        }

        // czas jaki osiagnal gracz
        final DurationUnit playerTimeDuration = new DurationUnit(Duration.ofMillis(playerTime));

        final IStatistic<DurationUnit> raceStatistic = this.getRaceStatistic(arena);
        final IStatisticHolder holder = this.statisticsManager.getPlayerHolder(player.getUniqueId());

        final boolean isFinished = IFinishHandler.checkFinished(arena);
        this.statisticsManager.getRecord(raceStatistic).whenComplete((bestRecord, throwable) ->
        {
            holder.record(raceStatistic, playerTimeDuration).whenComplete((record, throwable2) ->
            {
                final RaceMessage raceMessage = new RaceMessage(this.finishInfo, bestRecord, !isFinished);
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

        // podbijamy statystyke calkowitego przelecianego czasu
        final LongerTimeBetterStatistic totalRaceTimeStat = new LongerTimeBetterStatistic("elytra/totalRaceTime");
        holder.increment(totalRaceTimeStat, playerTimeDuration);

        if (isFinished)
        {
            arena.endGame();
        }
    }

    @Override
    public void playerQuit(final LocalArena arena, final Player player)
    {
        if (! IFinishHandler.checkFinished(arena))
        {
            return;
        }

        final IStatistic<DurationUnit> raceStatistic = this.getRaceStatistic(arena);
        this.statisticsManager.getRecord(raceStatistic).whenComplete((result, throwable) ->
        {
            final RaceMessage raceMessage = new RaceMessage(this.finishInfo, result, false);
            for (final Player playerInArena : arena.getPlayersManager().getPlayers())
            {
                raceMessage.print(playerInArena);
            }
        });

        arena.endGame();
    }

    private IStatistic<DurationUnit> getRaceStatistic(final LocalArena arena)
    {
        return new ShorterTimeBetterStatistic("elytra/race/" + arena.getWorld().getCurrentMapTemplate().getName());
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
        if (this.finishInfo.isEmpty())
        {
            // nikt nie ukonczyl areny, malo prawdopodobne, ale moze sie zdazyc
            // gdy arena zostanie wylaczona np. komenda
            return;
        }

        final RaceFinishInfo firstPlayer = this.finishInfo.get(0);
        final IStatisticHolder holder = this.statisticsManager.getPlayerHolder(firstPlayer.getUuid());

        final HigherNumberBetterStatistic totalElytraWins = new HigherNumberBetterStatistic("elytra/totalWins");
        holder.increment(totalElytraWins, new NumberUnit(1L));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("place", this.place).toString();
    }
}
