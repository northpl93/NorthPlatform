package pl.arieals.lobby.game.elytra;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DurationFormatUtils;

import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.arieals.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.type.LongerTimeBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.unit.DurationUnit;
import pl.arieals.lobby.game.HubScoreboardLayout;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ElytraHubScoreboard extends HubScoreboardLayout
{
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("DD'D' HH'H' mm'M'");
    @Inject @Messages("HubElytraRace")
    private MessagesBox        messages;
    @Inject
    private IStatisticsManager statisticsManager;

    @Override
    public void initContext(final IScoreboardContext context)
    {
        final IStatisticHolder playerHolder = this.statisticsManager.getPlayerHolder(context.getPlayer().getUniqueId());

        final LongerTimeBetterStatistic totalRaceTimeStat = new LongerTimeBetterStatistic("elytra/totalRaceTime");
        context.setCompletableFuture("raceTime", playerHolder.getBest(totalRaceTimeStat));

        final HigherNumberBetterStatistic totalScorePointsStat = new HigherNumberBetterStatistic("elytra/totalScorePoints");
        context.setCompletableFuture("scorePoints", playerHolder.getBest(totalScorePointsStat));

        final HigherNumberBetterStatistic winsStat = new HigherNumberBetterStatistic("elytra/totalWins");
        context.setCompletableFuture("wins", playerHolder.getBest(winsStat));
    }

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return this.messages.getString(context.getLocale(), "scoreboard.title");
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final ContentBuilder builder = IScoreboardLayout.builder();

        builder.box(this.messages).locale(context.getLocale());
        builder.add("");

        builder.translated("scoreboard.raceTime", this.parseTime(context.getCompletableFuture("raceTime")));
        builder.add("");

        builder.translated("scoreboard.scorePoints", this.parseNumber(context.getCompletableFuture("scorePoints")));
        builder.add("");

        builder.translated("scoreboard.wins", this.parseNumber(context.getCompletableFuture("wins")));
        builder.add("");

        builder.translated("scoreboard.money", this.getPlayerCurrency(context.getPlayer()));
        builder.add("");

        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    protected final String parseTime(final Optional<IRecord<DurationUnit>> optional)
    {
        final Duration duration = optional.map(durationUnitIRecord -> durationUnitIRecord.getValue().getValue()).orElse(Duration.ZERO);
        return DurationFormatUtils.formatDuration(duration.toMillis(), "d'D' H'H' m'M'");
    }
}
