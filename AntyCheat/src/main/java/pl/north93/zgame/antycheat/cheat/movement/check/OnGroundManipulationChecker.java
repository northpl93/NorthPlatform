package pl.north93.zgame.antycheat.cheat.movement.check;

import static pl.north93.zgame.antycheat.utils.DistanceUtils.entityDistanceToGround;
import static pl.north93.zgame.antycheat.utils.EntityUtils.isStandsOn;


import java.util.HashSet;
import java.util.Set;

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
 * Lapie: fly na ziemi, no-fall.
 */
public class OnGroundManipulationChecker implements EventAnalyser<ClientMoveTimelineEvent>
{
    private static final String ON_GROUND_TRUE_INCONSISTENCY  = "Player send onGround=true while he's effectively in air.";
    private static final String ON_GROUND_FALSE_INCONSISTENCY = "Player send onGround=false while he's effectively on ground.";
    /**
     * Lista problematycznych blokow uzywana w {@link #checkFlying(Player, PlayerTickInfo, RichEntityLocation)}.
     */
    private static final Material[] FLYING_PROBLEMATIC_BLOCKS = generateFlyingProblematicBlocks();
    /**
     * Wysokosc od jakiej gracz uznawany jest za na pewno uzywajacego no-fall.
     */
    private static final double NO_FALL_EDGE_HEIGHT = 1.5;
    /**
     * Przy sprawdzaniu czy gracz wysyla onGround=true w powietrzu sprawdzamy
     * czy gracz nie jest na krawedzi bloku.
     */
    private static final double GROW_AABB_BLOCK_EDGE = 0.3;
    /**
     * Gdy gracz znajduje sie przy scianie to powiekszony AABB powoduje ze odleglosc do ziemi jest ujemna.
     * Wykorzystujemy ten fakt aby lepiej lapac spidera.
     * Podczas parkourowania czasami wystepuja tu male wartosci ujemne, dlatego nie dajemy tu 0.
     */
    //private static final double TO_GROUND_BIGGER_AABB_SPIDER = - 0.5D;

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

        if (! event.isToOnGround())
        {
            // TODO Klient czasami sie buguje i wysyla w chuj onGround=false podczas stania...
            //return this.checkFlying(player, tickInfo, toLocation);
        }
        else if (! event.isFromOnGround() && event.isToOnGround())
        {
            return this.checkLanding(player, tickInfo, toLocation);
        }

        return SingleAnalysisResult.EMPTY;
    }

    // = = = Sprawdzanie gdy klient mówi że jest w powietrzu
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
        else if (this.isFlagsStairsOrHalfBlock(location.getFlags()))
        {
            // gdy gracz znajduje sie na schodach lub polblokach to klient wysyla syfy
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else if (this.isStandsOnProblematicBlockToCheckFlying(player, location))
        {
            // gdy gracz stoi na problematycznych blokach to moze wyslac ze jest w powietrzu.
            // Obliczony dystans wynosi wtedy 0. Uznajemy to za false-positive.
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else
        {
            final double toGroundWithBiggerAABB = entityDistanceToGround(player, location, - 0.25);
            if (toGround != toGroundWithBiggerAABB)
            {
                falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
            }
            else
            {
                falsePositiveProbability = FalsePositiveProbability.MEDIUM;
            }
        }

        analysisResult.addViolation(MovementViolation.ON_GROUND_INCONSISTENCY, ON_GROUND_FALSE_INCONSISTENCY, falsePositiveProbability);
        return analysisResult;
    }

    private boolean isStandsOnProblematicBlockToCheckFlying(final Player player, final RichEntityLocation location)
    {
        return isStandsOn(player, location, FLYING_PROBLEMATIC_BLOCKS);
    }

    // generuje tablice problematycznych blokow uzywana w checkFlying()
    private static Material[] generateFlyingProblematicBlocks()
    {
        final Set<Material> materials = new HashSet<>();
        materials.add(Material.SLIME_BLOCK);
        materials.addAll(BlockFlag.getMaterialsWithFlag(BlockFlag.STAIRS));
        materials.addAll(BlockFlag.getMaterialsWithFlag(BlockFlag.HALF));

        return materials.toArray(new Material[0]);
    }

    private boolean isOnGround(final double toGround)
    {
        return toGround <= 0;
    }

    // = = = Sprawdzanie gdy klient mówi że ląduje
    private SingleAnalysisResult checkLanding(final Player player, final PlayerTickInfo tickInfo, final RichEntityLocation location)
    {
        final double toGround = location.getDistanceToGround();
        if (! this.isInAir(toGround))
        {
            return SingleAnalysisResult.EMPTY;
        }

        final SingleAnalysisResult analysisResult = SingleAnalysisResult.create();

        final FalsePositiveProbability falsePositiveProbability;
        if (tickInfo.isShortAfterSpawn() || tickInfo.isShortAfterTeleport())
        {
            // krótko po spawnie i teleporcie klient wysyła śmieci w pakietach
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else if (location.isStandsOnEntity())
        {
            // gdy stoimy na jakims entity (lódka) to na 100% uznajemy to za false-positive
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else if (toGround == 0.5D || toGround == 1D)
        {
            // te dwie wartosci czesto wystepuja przy parkourowaniu dlatego
            // uznajemy je za false-positive
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else if (this.isFlagsStairsOrHalfBlock(location.getFlags()))
        {
            // gdy gracz znajduje sie na schodach lub polblokach to klient wysyla syfy
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else if (isStandsOn(player, location, Material.SLIME_BLOCK))
        {
            // gdy gracz ma pod nogami slime block to wysyla dziwne rzeczy, dlatego bezpiecznie
            // dajemy tu false positive.
            falsePositiveProbability = FalsePositiveProbability.DEFINITELY;
        }
        else
        {
            final double toGroundWithBiggerAABB = entityDistanceToGround(player, location, GROW_AABB_BLOCK_EDGE);
            if (toGround != toGroundWithBiggerAABB)
            {
                // gracz jest na skraju bloku
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
                falsePositiveProbability = FalsePositiveProbability.MEDIUM;
            }
        }

        analysisResult.addViolation(MovementViolation.ON_GROUND_INCONSISTENCY, ON_GROUND_TRUE_INCONSISTENCY, falsePositiveProbability);
        return analysisResult;
    }

    private boolean isInAir(final double toGroundDistance)
    {
        return toGroundDistance > 0;
    }

    private boolean isFlagsStairsOrHalfBlock(final long flags)
    {
        return BlockFlag.isFlagSet(flags, BlockFlag.STAIRS) || BlockFlag.isFlagSet(flags, BlockFlag.HALF);
    }
}
