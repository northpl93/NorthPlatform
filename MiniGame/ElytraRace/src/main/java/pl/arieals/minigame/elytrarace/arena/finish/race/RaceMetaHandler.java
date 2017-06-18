package pl.arieals.minigame.elytrarace.arena.finish.race;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.statistics.IRecordResult;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.arieals.api.minigame.shared.api.statistics.NumberStatistic;
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
    public void handle(final LocalArena arena, final Player player)
    {
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        if (playerData.isFinished())
        {
            // gracz juz przekroczyl linie mety, wiecej razy nie mozemy go obslugiwac
            return;
        }
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

        final IStatistic<NumberStatistic> raceStatistic = this.getRaceStatistic(arena);
        final NumberStatistic statistic = new NumberStatistic(playerTime);
        final CompletableFuture<IRecordResult> record = raceStatistic.record(player.getUniqueId(), statistic);

        final boolean isFinished = IFinishHandler.checkFinished(arena);

        record.whenComplete((result, throwable) ->
        {
            final RaceMessage raceMessage = new RaceMessage(this.finishInfo, result, !isFinished);
            if (isFinished)
            {
                raceMessage.print(player);
            }
            else
            {
                for (final Player playerInArena : arena.getPlayersManager().getPlayers())
                {
                    raceMessage.print(playerInArena);
                }
            }
        });

        if (isFinished)
        {
            arena.setGamePhase(GamePhase.POST_GAME);
        }
    }

    private IStatistic<NumberStatistic> getRaceStatistic(final LocalArena arena)
    {
        return this.statisticsManager.getStatistic(NumberStatistic.class, "elytra/race/" + arena.getWorld().getCurrentMapTemplate().getName(), true);
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
