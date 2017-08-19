package pl.arieals.minigame.elytrarace.arena.finish.race;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.arieals.api.minigame.shared.api.statistics.type.ShorterTimeBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.unit.DurationUnit;
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

        final IStatistic<DurationUnit> raceStatistic = this.getRaceStatistic(arena);
        final IStatisticHolder holder = this.statisticsManager.getHolder(player.getUniqueId());

        final CompletableFuture<IRecord<DurationUnit>> record = holder.record(raceStatistic, new DurationUnit(Duration.ofMillis(playerTime)));

        final boolean isFinished = IFinishHandler.checkFinished(arena);

        record.whenComplete((result, throwable) ->
        {
            // todo rework systemu statystyk; tutaj result jest zle, trzeba osobno pobrac (i cachowac?) rekord globalny
            final RaceMessage raceMessage = new RaceMessage(this.finishInfo, result, !isFinished);
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

        if (isFinished)
        {
            arena.setGamePhase(GamePhase.POST_GAME);
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
        this.statisticsManager.getBestRecord(raceStatistic).whenComplete((result, throwable) ->
        {
            final RaceMessage raceMessage = new RaceMessage(this.finishInfo, result, false);
            for (final Player playerInArena : arena.getPlayersManager().getPlayers())
            {
                raceMessage.print(playerInArena);
            }
        });

        arena.setGamePhase(GamePhase.POST_GAME);
    }

    private IStatistic<DurationUnit> getRaceStatistic(final LocalArena arena)
    {
        return new ShorterTimeBetterStatistic("elytra/race/" + arena.getWorld().getCurrentMapTemplate().getName());
    }

    @Override
    public void gameEnd(final LocalArena arena)
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("place", this.place).toString();
    }
}
