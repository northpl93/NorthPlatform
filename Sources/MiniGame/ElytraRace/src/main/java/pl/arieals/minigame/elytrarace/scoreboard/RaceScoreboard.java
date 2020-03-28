package pl.arieals.minigame.elytrarace.scoreboard;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.ShorterTimeBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.DurationUnit;

public class RaceScoreboard implements IScoreboardLayout
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("[m:]ss");
    @Inject
    private IStatisticsManager statisticsManager;
    @Inject @Messages("ElytraRace")
    private MessagesBox        msg;

    @Override
    public void initContext(final IScoreboardContext context)
    {
        final LocalArena arena = getArena(context.getPlayer());
        assert arena != null;

        final String statKey = "elytra/race/" + arena.getWorld().getCurrentMapTemplate().getName();
        final ShorterTimeBetterStatistic statistic = new ShorterTimeBetterStatistic(statKey);

        // odczytujemy asynchronicznie sredni czas graczy na tej mapie
        context.setCompletableFuture("avgTime", this.statisticsManager.getAverage(statistic));
    }

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return "&e&lTime Attack";
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final INorthPlayer player = context.getPlayer();

        final LocalArena arena = getArena(player);
        final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);
        if (arena == null || playerData == null)
        {
            return Collections.emptyList();
        }

        final ElytraRaceArena arenaData = arena.getArenaData();
        final ContentBuilder builder = IScoreboardLayout.builder();

        builder.box(this.msg).locale(player.getLocale());
        builder.add("");

        builder.translated("scoreboard.race.time", arena.getTimer().humanReadableTimeAfterStart());
        builder.translated("scoreboard.race.avg_time", this.getAvgTime(context));
        builder.add("");
        builder.translated("scoreboard.race.checkpoint", playerData.getCheckpointNumber(), arenaData.getMaxCheckpoints());
        builder.add("");
        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    private String getAvgTime(final IScoreboardContext ctx)
    {
        final Optional<DurationUnit> avgTime = ctx.getCompletableFuture("avgTime");
        return avgTime.map(record ->
        {
            final Duration averageDuration = record.getValue();
            final LocalTime time = LocalTime.ofNanoOfDay(averageDuration.toNanos());

            return FORMAT.format(time);
        }).orElse("?");
    }

    @Override
    public int updateEvery()
    {
        return 10;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
