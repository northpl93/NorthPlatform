package pl.north93.northplatform.api.minigame.shared.impl.statistics;

import java.time.Duration;
import java.util.function.BiConsumer;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.northplatform.api.minigame.shared.api.statistics.filter.BestRecordFilter;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.ShorterTimeBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.DurationUnit;

public class StatisticsTestCmd extends NorthCommand
{
    @Inject
    private IStatisticsManager statisticsManager;

    public StatisticsTestCmd()
    {
        super("teststats");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final ShorterTimeBetterStatistic statistic = new ShorterTimeBetterStatistic("elytra/race/temple");

        this.statisticsManager.getRecord(statistic, new BestRecordFilter()).whenComplete(new BiConsumer<IRecord<Duration, DurationUnit>, Throwable>()
        {
            @Override
            public void accept(final IRecord<Duration, DurationUnit> durationDurationUnitIRecord, final Throwable throwable)
            {
                sender.sendMessage(durationDurationUnitIRecord.toString());

                sender.sendMessage("- - -");
            }
        });

        this.statisticsManager.getRanking(statistic, 10, new BestRecordFilter()).whenComplete((ranking, throwable) ->
        {
            for (final IRecord<Duration, DurationUnit> place : ranking.getPlaces())
            {
                sender.sendMessage(place.toString());
            }
        });
    }
}
