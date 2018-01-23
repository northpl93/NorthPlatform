package pl.north93.zgame.antycheat.cheat.fight.check;

import java.time.Duration;
import java.time.Instant;

import org.bukkit.Bukkit;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyser;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyserConfig;
import pl.north93.zgame.antycheat.cheat.fight.FightViolation;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent.EntityAction;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;

public class AttackFrequencyCheck implements TimelineAnalyser
{
    private static final int MIN_HITS_TO_ANALYSE = 3;
    @Override
    public void configure(final TimelineAnalyserConfig config)
    {
        config.setScope(TimelineAnalyserConfig.Scope.FIVE_SECONDS);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final TimelineWalker timelineWalker)
    {
        final LongList delays = this.collectAllDelays(timelineWalker);

        final int size = delays.size();
        if (size < MIN_HITS_TO_ANALYSE)
        {
            return null; // jak za mało ataków to nic nie analizujemy
        }

        final long sum = this.sum(delays);

        final double avg = sum / size;
        final double standardDeviation = this.standardDeviation(delays, avg);

        Bukkit.broadcastMessage(data.getPlayer().getName() + " C:" + size + " AVG:" + avg + " SD:" + standardDeviation);

        final SingleAnalysisResult singleAnalysisResult = SingleAnalysisResult.create();
        this.punishPlayerForHitCount(size, singleAnalysisResult);
        this.punishPlayerForAvgDelayTime(avg, singleAnalysisResult);
        this.punishPlayerForDeviation(standardDeviation, singleAnalysisResult);

        return singleAnalysisResult;
    }

    private void punishPlayerForHitCount(final int hits, final SingleAnalysisResult result)
    {
        if (hits <= 35)
        {
            return;
        }

        final FalsePositiveProbability falsePositiveProbability;
        if (hits >= 45)
        {
            falsePositiveProbability = FalsePositiveProbability.LOW;
        }
        else
        {
            falsePositiveProbability = FalsePositiveProbability.MEDIUM;
        }

        result.addViolation(FightViolation.HIT_RATIO, "Too many hits", falsePositiveProbability);
    }

    private void punishPlayerForAvgDelayTime(final double avg, final SingleAnalysisResult result)
    {
        if (avg > 125)
        {
            return;
        }

        final FalsePositiveProbability falsePositiveProbability;
        if (avg >= 75)
        {
            falsePositiveProbability = FalsePositiveProbability.HIGH;
        }
        else if (avg >= 25)
        {
            falsePositiveProbability = FalsePositiveProbability.MEDIUM;
        }
        else
        {
            falsePositiveProbability = FalsePositiveProbability.LOW;
        }

        result.addViolation(FightViolation.HIT_RATIO, "Too short time between hits", falsePositiveProbability);
    }

    private void punishPlayerForDeviation(final double standardDeviation, final SingleAnalysisResult result)
    {
        if (standardDeviation > 100)
        {
            return;
        }

        final FalsePositiveProbability falsePositiveProbability;
        if (standardDeviation >= 50)
        {
            falsePositiveProbability = FalsePositiveProbability.HIGH;
        }
        else if (standardDeviation >= 15)
        {
            falsePositiveProbability = FalsePositiveProbability.MEDIUM;
        }
        else
        {
            falsePositiveProbability = FalsePositiveProbability.LOW;
        }

        result.addViolation(FightViolation.HIT_RATIO, "Small standard deviation", falsePositiveProbability);
    }

    private double standardDeviation(final LongList numbers, final double average)
    {
        final int size = numbers.size();

        double deviation = 0, sum = 0;
        for (int i = 0; i < size; i++)
        {
            sum = sum + Math.pow(numbers.getLong(i) - average, 2);
            deviation = Math.sqrt(sum / size);
        }

        return deviation;
    }

    private long sum(final LongList longs)
    {
        long sum = 0;
        final LongListIterator iterator = longs.iterator();
        while (iterator.hasNext())
        {
            sum += iterator.nextLong();
        }

        return sum;
    }

    private LongList collectAllDelays(final TimelineWalker timelineWalker)
    {
        final LongArrayList delays = new LongArrayList(16);

        InteractWithEntityTimelineEvent event = null;
        while (timelineWalker.hasPrevious())
        {
            final InteractWithEntityTimelineEvent olderEvent = timelineWalker.previous(InteractWithEntityTimelineEvent.class);
            if (olderEvent == null)
            {
                break;
            }
            if (olderEvent.getAction() != EntityAction.ATTACK)
            {
                continue;
            }

            if (event != null)
            {
                final Instant timeOfNewerEvent = event.getCreationTime();
                final Instant timeOfOlderEvent = olderEvent.getCreationTime();

                final long delay = Duration.between(timeOfOlderEvent, timeOfNewerEvent).toMillis();
                delays.add(delay);
            }
            event = olderEvent;
        }

        return delays;
    }
}
