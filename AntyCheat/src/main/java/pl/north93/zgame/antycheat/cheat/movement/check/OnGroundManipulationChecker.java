package pl.north93.zgame.antycheat.cheat.movement.check;

import static pl.north93.zgame.antycheat.utils.DistanceUtils.entityDistanceToGround;


import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.cheat.movement.MovementViolation;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.utils.location.RichEntityLocation;

/**
 * Weryfikuje czy klient wysyla poprawny parametr on ground w pakietach ruchu.
 * Lapie: fly na ziemi, no-fall, spider.
 */
public class OnGroundManipulationChecker implements EventAnalyser<ClientMoveTimelineEvent>
{
    private static final String ON_GROUND_TRUE_INCONSISTENCY  = "Player send onGround=true while he's effectively in air.";
    private static final String ON_GROUND_FALSE_INCONSISTENCY = "Player send onGround=false while he's effectively on ground.";
    /**
     * Odleglosc w jakiej gracz jest uznawany za stojacego na ziemi.
     * Uzywane w {@link #isOnGround(double)}.
     */
    private static final double IS_ON_GROUND_EPSILON = 0.1;
    /**
     * Wysokosc od jakiej gracz uznawany jest za na pewno uzywajacego no-fly, spidera.
     */
    private static final double NO_FALL_EDGE_HEIGHT = 1.25;
    /**
     * Gdy gracz znajduje sie przy scianie to powiekszony AABB powoduje ze odleglosc do ziemi jest ujemna.
     * Wykorzystujemy ten fakt aby lepiej lapac spidera.
     * Podczas parkourowania czasami wystepuja tu male wartosci ujemne, dlatego nie dajemy tu 0.
     */
    private static final double TO_GROUND_BIGGER_AABB_SPIDER = - 0.5D;

    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.whitelistEvent(ClientMoveTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final ClientMoveTimelineEvent event)
    {
        final Player player = data.getPlayer();
        final RichEntityLocation toLocation = event.getTo();

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
    private SingleAnalysisResult checkFlying(final Player player, final PlayerTickInfo tickInfo, final RichEntityLocation location)
    {
        final double toGround = location.getDistanceToGround();
        if (! this.isOnGround(toGround))
        {
            // gracz nie jest na ziemi, wszystko ok
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
    private SingleAnalysisResult checkLanding(final Player player, final RichEntityLocation location)
    {
        final double toGround = location.getDistanceToGround();
        if (! this.isInAir(toGround))
        {
            return SingleAnalysisResult.EMPTY;
        }

        final SingleAnalysisResult analysisResult = SingleAnalysisResult.create();

        final FalsePositiveProbability falsePositiveProbability;
        if (location.isStandsOnEntity())
        {
            // gdy stoimy na jakims entity (lódka) to na 100% uznajemy to za false-positive
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else if (toGround > NO_FALL_EDGE_HEIGHT)
        {
            // gdy klient wysyla do nas informacje o ladowaniu bedac powyzej ilus tam klockow
            // to prawie na pewno jest to no-fall lub spider.
            falsePositiveProbability = FalsePositiveProbability.LOW;
        }
        else
        {
            final double toGroundWithBiggerAABB = entityDistanceToGround(player, location, 0.3);
            //Bukkit.broadcastMessage("toGround=" + toGround + " withBigger=" + toGroundWithBiggerAABB);
            if (toGroundWithBiggerAABB < TO_GROUND_BIGGER_AABB_SPIDER)
            {
                // gracz jest przy scianie; bardzo prawdopodobne ze korzysta z spidera
                falsePositiveProbability = FalsePositiveProbability.LOW;
            }
            else if (this.isInAir(toGroundWithBiggerAABB))
            {
                // wiekszy AABB ciagle jest w powietrzu, teraz sprawdzamy false-positive
                // takze zwiazane z parkourowaniem
                if (toGround == 0.5D || toGround == 1D)
                {
                    // te dwie wartosci czesto wystepuja przy parkourowaniu dlatego
                    // uznajemy je za false-positive
                    falsePositiveProbability = FalsePositiveProbability.HIGH;
                }
                else
                {
                    // w pozostalych przypadkach zostajemy przy bezpiecznym poziomie medium
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
