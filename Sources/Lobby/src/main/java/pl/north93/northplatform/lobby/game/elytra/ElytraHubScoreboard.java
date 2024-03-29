package pl.north93.northplatform.lobby.game.elytra;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DurationFormatUtils;

import pl.north93.northplatform.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.LongerTimeBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.DurationUnit;
import pl.north93.northplatform.lobby.game.HubScoreboardLayout;

public class ElytraHubScoreboard extends HubScoreboardLayout
{
    @Inject @Messages("HubElytraRace")
    private MessagesBox messages;
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

        //builder.translated("scoreboard.chests", this.getPlayerChests(context.getPlayer(), "elytra"));
        //builder.add(""); // todo dodac to gdy wlaczymy skrzynki elytra i znajdziemy miejsce na SB

        builder.translated("scoreboard.money", this.getPlayerCurrency(context.getPlayer()));
        builder.add("");

        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    protected final String parseTime(final Optional<IRecord<Duration, DurationUnit>> optional)
    {
        final Duration duration = optional.map(durationUnitIRecord -> durationUnitIRecord.getValue().getValue()).orElse(Duration.ZERO);
        return DurationFormatUtils.formatDuration(duration.toMillis(), "d'D' H'H' m'M'");
    }
}
