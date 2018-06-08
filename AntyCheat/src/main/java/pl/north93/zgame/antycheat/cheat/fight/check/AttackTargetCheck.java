package pl.north93.zgame.antycheat.cheat.fight.check;

import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyserConfig;
import pl.north93.zgame.antycheat.cheat.fight.FightViolation;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.Timeline;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;
import pl.north93.zgame.antycheat.utils.AABB;
import pl.north93.zgame.antycheat.utils.EntityUtils;
import pl.north93.zgame.antycheat.utils.RayTrace;
import pl.north93.zgame.antycheat.utils.block.BlockUtils;
import pl.north93.zgame.antycheat.utils.location.IPosition;
import pl.north93.zgame.antycheat.utils.location.RichEntityLocation;

public class AttackTargetCheck implements EventAnalyser<InteractWithEntityTimelineEvent>
{
    private static final String ENTITY_NOT_TARGETED = "Attacked not targeted entity";
    private static final String TOO_BIG_DISTANCE    = "Attacked entity from too big distance";

    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.whitelistEvent(InteractWithEntityTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final InteractWithEntityTimelineEvent event)
    {
        if (this.shouldSkip(tickInfo))
        {
            return null;
        }

        final SingleAnalysisResult analysisResult = SingleAnalysisResult.create();

        final Entity targetEntity = event.getEntity();
        if (! this.shouldProcessEntity(targetEntity))
        {
            return null;
        }

        final IPosition targetPosition = IPosition.fromBukkit(targetEntity.getLocation());
        final AABB targetAABB = EntityUtils.getAABBOfEntityInLocation(targetEntity, targetPosition).grow(0.1, 0.1, 0.1);

        final RichEntityLocation attackerLocation = this.findLocationBeforeHit(data, tickInfo, event);
        final RayTrace rayTrace = this.createRayTrace(attackerLocation, tickInfo);

        final IntersectionResult intersection = this.intersectionWithAabbWithoutBlocks(targetEntity.getWorld(), rayTrace, targetAABB);
        if (intersection.getLocation() == null)
        {
            // wykonujemy dodatkowe checki poniewaz przy dynamicznym machaniu myszka minecraft
            // jest mocno nieprecyzyjny i lapamiy false positives
            this.additionalAttackWidthCheck(data, tickInfo, targetAABB, analysisResult);
            return analysisResult;
        }
        else if (intersection.isBlocked())
        {
            // gracz wycelowal w entity, ale droga jest zablokowana
            analysisResult.addViolation(FightViolation.HIT_TARGET, ENTITY_NOT_TARGETED, FalsePositiveProbability.MEDIUM);
        }

        // sprawdzamy czy gracz nie bije z zbyt duzej odleglosci
        final Vector attackVector = this.calculateAttackVector(attackerLocation, tickInfo, intersection.getLocation());
        final double distance = attackVector.length();
        this.punishForExceededDistance(distance, analysisResult);

        return analysisResult;
    }

    private boolean shouldProcessEntity(final @Nullable Entity entity)
    {
        if (entity == null)
        {
            return false;
        }

        return entity instanceof LivingEntity;
    }

    // uzywa kilku lokalizacji z jednego ticku aby zmniejszyc prawdopodobienstwo pomylki
    private void additionalAttackWidthCheck(final PlayerData data, final PlayerTickInfo tickInfo, final AABB attackedAabb, final SingleAnalysisResult analysisResult)
    {
        final TimelineWalker walkerForScope = data.getTimeline().createWalkerForScope(TimelineAnalyserConfig.Scope.TICK);

        final ClientMoveTimelineEvent lastMovement = walkerForScope.last(ClientMoveTimelineEvent.class);
        final ClientMoveTimelineEvent firstMovement = walkerForScope.first(ClientMoveTimelineEvent.class);

        if (firstMovement == null || lastMovement == null)
        {
            analysisResult.addViolation(FightViolation.HIT_TARGET, ENTITY_NOT_TARGETED, FalsePositiveProbability.HIGH);
            return;
        }

        final RichEntityLocation firstLocation = firstMovement.getFrom();
        final RichEntityLocation lastLocation = lastMovement.getTo();

        final Collection<RayTrace> rayTraces;
        if (firstMovement == lastMovement)
        {
            rayTraces = Collections.singletonList(this.createRayTrace(firstMovement.getTo(), tickInfo));
        }
        else
        {
            final RayTrace trace1 = this.createRayTrace(firstLocation, tickInfo);
            final RayTrace trace2 = this.createRayTraceBetweenDirections(firstLocation, lastLocation, tickInfo);
            final RayTrace trace3 = this.createRayTrace(lastLocation, tickInfo);

            rayTraces = Arrays.asList(trace1, trace2, trace3);
        }

        if (this.verifyAnyVectorIsValid(rayTraces, data.getPlayer().getWorld(), attackedAabb))
        {
            return;
        }

        analysisResult.addViolation(FightViolation.HIT_TARGET, ENTITY_NOT_TARGETED, FalsePositiveProbability.HIGH);
    }

    // sprawdza czy jakikolwiek z raytracers jest dobry (przechodzi przez entity i nie koliduje z blokiem)
    private boolean verifyAnyVectorIsValid(final Collection<RayTrace> rayTracers, final World world, final AABB attacked)
    {
        for (final RayTrace rayTracer : rayTracers)
        {
            final IntersectionResult intersection = this.intersectionWithAabbWithoutBlocks(world, rayTracer, attacked);
            if (intersection.getLocation() == null || intersection.isBlocked())
            {
                continue;
            }

            return true;
        }

        return false;
    }

    private void punishForExceededDistance(final double distance, final SingleAnalysisResult result)
    {
        final double exceededDistance = distance - 3;
        if (exceededDistance <= 0.1)
        {
            return;
        }

        final FalsePositiveProbability falsePositiveProbability;
        if (exceededDistance >= 0.5)
        {
            falsePositiveProbability = FalsePositiveProbability.LOW;
        }
        else if (exceededDistance >= 0.25)
        {
            falsePositiveProbability = FalsePositiveProbability.MEDIUM;
        }
        else
        {
            falsePositiveProbability = FalsePositiveProbability.HIGH;
        }

        result.addViolation(FightViolation.HIT_TARGET, TOO_BIG_DISTANCE, falsePositiveProbability);
    }

    private boolean shouldSkip(final PlayerTickInfo tickInfo)
    {
        final GameMode gameMode = tickInfo.getOwner().getGameMode();
        return gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR;
    }

    private RayTrace createRayTrace(final RichEntityLocation location, final PlayerTickInfo tickInfo)
    {
        final Vector start = this.getEyeLocation(location, tickInfo);
        final Vector direction = location.getDirection();

        return new RayTrace(start, direction);
    }

    private RayTrace createRayTraceBetweenDirections(final RichEntityLocation l1, final RichEntityLocation l2, final PlayerTickInfo tickInfo)
    {
        final Vector location = this.getEyeLocation(l1, tickInfo);
        final Vector crossProduct = l1.getDirection().crossProduct(l2.getDirection());

        return new RayTrace(location, crossProduct);
    }

    private Vector calculateAttackVector(final RichEntityLocation location, final PlayerTickInfo tickInfo, final Vector target)
    {
        return this.getEyeLocation(location, tickInfo).subtract(target);
    }

    private Vector getEyeLocation(final RichEntityLocation location, final PlayerTickInfo tickInfo)
    {
        return new Vector(location.getX(), location.getY() + tickInfo.getOwner().getEyeHeight(), location.getZ());
    }

    private RichEntityLocation findLocationBeforeHit(final PlayerData playerData, final PlayerTickInfo tickInfo, final InteractWithEntityTimelineEvent event)
    {
        final Timeline timeline = playerData.getTimeline();

        final TimelineWalker walker = timeline.createWalkerForScope(TimelineAnalyserConfig.Scope.ALL);
        if (walker.find(event))
        {
            final ClientMoveTimelineEvent moveEvent = walker.previous(ClientMoveTimelineEvent.class);
            if (moveEvent != null)
            {
                return moveEvent.getTo();
            }
        }

        return tickInfo.getProperties().getLocation();
    }

    private IntersectionResult intersectionWithAabbWithoutBlocks(final World world, final RayTrace rayTrace, final AABB entityAabb)
    {
        boolean blocked = false;
        for (final Vector location : rayTrace.traverse(3.25, 0.1))
        {
            if (entityAabb.intersects(location))
            {
                return new IntersectionResult(location, blocked);
            }

            if (blocked)
            {
                // jak juz ustawilismy flage blocked to nie sprawdzamy dalej bloków dla optymalizacji
                continue;
            }

            final Block block = world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            if (block.getType() != Material.AIR)
            {
                final AABB blockAABB = BlockUtils.getExactBlockAABB(block);
                if (blockAABB.intersects(location))
                {
                    blocked = true;
                }
            }
        }

        return new IntersectionResult(null, blocked);
    }

    private static final class IntersectionResult
    {
        private final Vector location;
        private final boolean block; // czy zablokowane przez blok

        public IntersectionResult(final Vector location, final boolean block)
        {
            this.location = location;
            this.block = block;
        }

        public Vector getLocation()
        {
            return this.location;
        }

        public boolean isBlocked()
        {
            return this.block;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("location", this.location).append("block", this.block).toString();
        }
    }
}
