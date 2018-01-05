package pl.north93.zgame.antycheat.cheat.movement;

import static pl.north93.zgame.antycheat.utils.DistanceUtils.entityDistanceToGround;


import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.event.impl.PlayerMoveTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;

/**
 * Weryfikuje czy klient wysyla poprawny parametr on ground w pakietach ruchu.
 * Lapie: fly na ziemi, no-fall, spider.
 */
public class OnGroundManipulationChecker implements EventAnalyser<PlayerMoveTimelineEvent>
{
    private static final String ON_GROUND_TRUE_INCONSISTENCY  = "Player send onGround=true while he's effectively in air.";
    private static final String ON_GROUND_FALSE_INCONSISTENCY = "Player send onGround=false while he's effectively on ground.";
    private static final double IS_ON_GROUND_EPSILON = 0.1;

    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.whitelistEvent(PlayerMoveTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final PlayerMoveTimelineEvent event)
    {
        final Player player = data.getPlayer();
        final Location toLocation = event.getTo();

        if (event.isFromOnGround() && ! event.isToOnGround())
        {
            return this.checkFlying(player, tickInfo, toLocation);
        }
        else if (! event.isFromOnGround() && event.isToOnGround())
        {
            return this.checkLanding(player, toLocation);
        }

        return SingleAnalysisResult.EMPTY;
    }

    // = = = Sprawdzanie gdy klient mówi że startuje z ziemi
    private SingleAnalysisResult checkFlying(final Player player, final PlayerTickInfo tickInfo, final Location location)
    {
        final double toGround = entityDistanceToGround(player, location);
        if (! this.isOnGround(toGround))
        {
            return SingleAnalysisResult.EMPTY;
        }

        final SingleAnalysisResult analysisResult = SingleAnalysisResult.create();

        final FalsePositiveProbability falsePositiveProbability;
        if (tickInfo.isShortAfterSpawn())
        {
            // krótko po spawnie klient wysyła śmieci w pakietach
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else
        {
            final double toGroundWithSmallerAABB = entityDistanceToGround(player, location, -0.2);
            if (this.isOnGround(toGroundWithSmallerAABB))
            {
                if (toGround == 0 && toGroundWithSmallerAABB == 0)
                {
                    falsePositiveProbability = FalsePositiveProbability.LOW;
                }
                else
                {
                    falsePositiveProbability = FalsePositiveProbability.HIGH;
                }
            }
            else
            {
                falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
            }
        }

        analysisResult.addViolation(MovementViolation.ON_GROUND_INCONSISTENCY, ON_GROUND_FALSE_INCONSISTENCY, falsePositiveProbability);
        return analysisResult;
    }

    private boolean isOnGround(final double toGround)
    {
        return toGround <= IS_ON_GROUND_EPSILON;
    }

    // = = = Sprawdzanie gdy klient mówi że ląduje
    private SingleAnalysisResult checkLanding(final Player player, final Location location)
    {
        final double toGround = entityDistanceToGround(player, location);
        if (! this.isInAir(toGround))
        {
            return SingleAnalysisResult.EMPTY;
        }

        final SingleAnalysisResult analysisResult = SingleAnalysisResult.create();

        final FalsePositiveProbability falsePositiveProbability;
        if (toGround > 1.5)
        {
            // gdy klient wysyla do nas informacje o ladowaniu bedac powyzej 1.5 klocka
            // to prawie na pewno jest to no-fall lub spider.
            falsePositiveProbability = FalsePositiveProbability.LOW;
        }
        else
        {
            final double toGroundWithBiggerAABB = entityDistanceToGround(player, location, 0.5);
            if (this.isInAir(toGroundWithBiggerAABB))
            {
                // wiekszy AABB ciagle jest w powietrzu, teraz sprawdzamy false-positive
                // takze zwiazane z parkourowaniem
                if (toGround == 0.5D || toGround == 1D)
                {
                    falsePositiveProbability = FalsePositiveProbability.HIGH;
                }
                else
                {
                    falsePositiveProbability = FalsePositiveProbability.MEDIUM;
                }
            }
            else
            {
                // gdy wiekszy AABB jednak jest na ziemi to uznajemy to za false-positive
                // czesto wystepuje przy parkourach gdy ladujemy na krawedzi klocka
                falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
            }
        }

        analysisResult.addViolation(MovementViolation.ON_GROUND_INCONSISTENCY, ON_GROUND_TRUE_INCONSISTENCY, falsePositiveProbability);
        return analysisResult;
    }

    private boolean isInAir(final double toGroundDistance)
    {
        return toGroundDistance > 0;
    }
}
