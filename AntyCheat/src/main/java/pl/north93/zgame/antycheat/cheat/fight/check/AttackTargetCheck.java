package pl.north93.zgame.antycheat.cheat.fight.check;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.cheat.fight.FightViolation;
import pl.north93.zgame.antycheat.event.impl.InteractWithEntityTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
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
        final IPosition targetPosition = IPosition.fromBukkit(targetEntity.getLocation());
        final AABB targetAABB = EntityUtils.getAABBOfEntityInLocation(targetEntity, targetPosition);

        final RayTrace rayTrace = this.createRayTrace(tickInfo);

        final IntersectionResult intersection = this.intersectionWithAabbWithoutBlocks(targetEntity.getWorld(), rayTrace, targetAABB.grow(0.1, 0.1, 0.1));
        if (intersection.isBlocked())
        {
            analysisResult.addViolation(FightViolation.HIT_TARGET, ENTITY_NOT_TARGETED, FalsePositiveProbability.MEDIUM);
            return analysisResult;
        }
        else if (intersection.getLocation() == null)
        {
            analysisResult.addViolation(FightViolation.HIT_TARGET, ENTITY_NOT_TARGETED, FalsePositiveProbability.HIGH);
            return analysisResult;
        }

        final Vector attackVector = this.calculateAttackVector(tickInfo, intersection.getLocation());
        final double distance = attackVector.length();
        this.punishForExceededDistance(distance, analysisResult);

        //rayTrace.highlight(targetEntity.getWorld(), distance, 0.25);
        return analysisResult;
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

    private RayTrace createRayTrace(final PlayerTickInfo tickInfo)
    {
        final Vector start = this.getEyeLocation(tickInfo);

        final RichEntityLocation location = tickInfo.getProperties().getLocation();
        final Vector direction = location.getDirection();

        return new RayTrace(start, direction);
    }

    private Vector calculateAttackVector(final PlayerTickInfo tickInfo, final Vector target)
    {
        return this.getEyeLocation(tickInfo).subtract(target);
    }

    private Vector getEyeLocation(final PlayerTickInfo tickInfo)
    {
        final double eyeHeight = tickInfo.getOwner().getEyeHeight();
        final RichEntityLocation location = tickInfo.getProperties().getLocation();
        return new Vector(location.getX(), location.getY() + eyeHeight, location.getZ());
    }

    private IntersectionResult intersectionWithAabbWithoutBlocks(final World world, final RayTrace rayTrace, final AABB entityAabb)
    {
        for (final Vector location : rayTrace.traverse(3.25, 0.2))
        {
            if (entityAabb.intersects(location))
            {
                return new IntersectionResult(location, false);
            }

            final Block block = world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            if (block.getType() == Material.AIR)
            {
                continue;
            }

            final AABB blockAABB = BlockUtils.getExactBlockAABB(block);
            if (blockAABB.intersects(location))
            {
                return new IntersectionResult(location, true);
            }
        }

        return new IntersectionResult(null, false);
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
