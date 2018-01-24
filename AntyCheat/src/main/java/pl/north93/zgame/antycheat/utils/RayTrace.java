package pl.north93.zgame.antycheat.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.util.Vector;

public final class RayTrace
{
    //origin = start position
    //direction = direction in which the raytrace will go
    private final Vector origin, direction;

    public RayTrace(final Vector origin, final Vector direction)
    {
        this.origin = origin;
        this.direction = direction;
    }

    //get a point on the raytrace at X blocks away
    public Vector getPosition(final double blocksAway)
    {
        return this.origin.clone().add(this.direction.clone().multiply(blocksAway));
    }

    //checks if a position is on contained within the position
    public boolean isOnLine(final Vector position)
    {
        final double t = (position.getX() - this.origin.getX()) / this.direction.getX();
        return position.getBlockY() == this.origin.getY() + (t * this.direction.getY()) && position.getBlockZ() == this.origin.getZ() + (t * this.direction.getZ());
    }

    //get all postions on a raytrace
    public List<Vector> traverse(final double blocksAway, final double accuracy)
    {
        final List<Vector> positions = new ArrayList<>();
        for (double d = 0; d <= blocksAway; d += accuracy)
        {
            positions.add(this.getPosition(d));
        }
        return positions;
    }

    //bounding box instead of vector
    public Vector positionOfIntersection(final AABB boundingBox, final double blocksAway, final double accuracy)
    {
        final List<Vector> positions = this.traverse(blocksAway, accuracy);
        for (Vector position : positions)
        {
            if (boundingBox.intersects(position))
            {
                return position;
            }
        }
        return null;
    }

    //bounding box instead of vector
    public boolean intersects(final AABB boundingBox, final double blocksAway, final double accuracy)
    {
        final List<Vector> positions = this.traverse(blocksAway, accuracy);
        for (Vector position : positions)
        {
            if (boundingBox.intersects(position))
            {
                return true;
            }
        }
        return false;
    }

    //debug / effects
    public void highlight(final World world, final double blocksAway, final double accuracy)
    {
        for (Vector position : this.traverse(blocksAway, accuracy))
        {
            world.playEffect(position.toLocation(world), Effect.COLOURED_DUST, 0);
        }
    }
}