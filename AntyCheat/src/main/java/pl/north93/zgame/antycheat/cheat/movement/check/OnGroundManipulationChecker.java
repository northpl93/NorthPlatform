package pl.north93.zgame.antycheat.cheat.movement.check;

import static pl.north93.zgame.antycheat.utils.DistanceUtils.entityDistanceToGround;
import static pl.north93.zgame.antycheat.utils.EntityUtils.isStandsOn;


import org.bukkit.Material;
import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.cheat.movement.MovementViolation;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.utils.block.BlockFlag;
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
        if (tickInfo.isShortAfterSpawn() || tickInfo.isShortAfterTeleport())
        {
            // krótko po spawnie i teleporcie klient wysyła śmieci w pakietach
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else if (BlockFlag.isFlagSet(location.getFlags(), BlockFlag.LIQUID))
        {
            // gdy jestesmy w wodzie i probujemy wyjsc to klient wysyla tu totalny syf
            // a i tak w wodzie ten check za bardzo nie ma sensu...
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else if (isStandsOn(player, location, Material.SLIME_BLOCK))
        {
            // gdy gracz stoi na slimeblocku to wysyla ze jest w powietrzu. Obliczony dystans wynosi wtedy 0.
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else
        {
            final double toGroundWithSmallerAABB = entityDistanceToGround(player, location, -0.25);
            if (this.isOnGround(toGroundWithSmallerAABB))
            {
                if (BlockFlag.isFlagSet(location.getFlags(), BlockFlag.STAIRS))
                {
                    falsePositiveProbability = FalsePositiveProbability.HIGH;
                }
                else if (toGround != 0 || toGroundWithSmallerAABB != 0)
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
                falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
            }
        }

        analysisResult.addViolation(MovementViolation.ON_GROUND_INCONSISTENCY, ON_GROUND_FALSE_INCONSISTENCY, falsePositiveProbability);
        return analysisResult;
    }

    private boolean isOnGround(final double toGround)
    {
        return toGround == 0; // todo powaznie rozwazyc toGround <= 0.1
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
        else
        {
            final double toGroundWithBiggerAABB = entityDistanceToGround(player, location, 0.25);
            //Bukkit.broadcastMessage("toGround=" + toGround + " withBigger=" + toGroundWithBiggerAABB);
            if (toGroundWithBiggerAABB < TO_GROUND_BIGGER_AABB_SPIDER)
            {
                // gracz jest przy scianie; bardzo prawdopodobne ze korzysta z spidera,
                // ale gdy jestesmy w wodzie to tu pojawia sie duzy spam false-positive
                if (BlockFlag.isFlagSet(location.getFlags(), BlockFlag.LIQUID))
                {
                    falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
                }
                else
                {
                    falsePositiveProbability = FalsePositiveProbability.LOW;
                }
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
                else if (toGround > NO_FALL_EDGE_HEIGHT)
                {
                    // gdy klient wysyla do nas informacje o ladowaniu bedac powyzej ilus tam klockow
                    // to prawie na pewno jest to no-fall lub spider.
                    falsePositiveProbability = FalsePositiveProbability.LOW;
                }
                else if (isStandsOn(player, location, Material.SLIME_BLOCK))
                {
                    // gdy gracz ma pod nogami slime block to wysyla dziwne rzeczy, dlatego bezpiecznie
                    // dajemy tu poziom HIGH.
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
