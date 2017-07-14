package pl.arieals.api.minigame.server.gamehost.deathmatch;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import org.diorite.utils.math.DioriteRandomUtils;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.north93.zgame.api.bukkit.utils.FastBlockOp;
import pl.north93.zgame.api.bukkit.utils.nms.NorthFallingBlock;

public class ArenaDestroyerTask extends BukkitRunnable
{
    private final LocalArena  arena;
    private final BlockWalker walker;
    private final Location    arenaCenter;

    public ArenaDestroyerTask(final Location startLocation, final LocalArena arena)
    {
        this.walker = new BlockWalker(startLocation);
        this.arena = arena;
        this.arenaCenter = new Location(arena.getWorld().getCurrentWorld(), 0, 165, 37);
    }

    @Override
    public void run()
    {
        if (this.arena.getDeathMatch().getState() != DeathMatchState.STARTED)
        {
            this.cancel();
            return;
        }

        final Block next = this.walker.next();
        if (next == null)
        {
            this.cancel();
            return;
        }

        Block highestBlockAt = next.getWorld().getHighestBlockAt(next.getX(), next.getZ());
        while (true)
        {
            if (highestBlockAt.getY() <= 0)
            {
                break;
            }
            if (highestBlockAt.isEmpty())
            {
                highestBlockAt = highestBlockAt.getRelative(BlockFace.DOWN);
                continue;
            }
            this.fancyDestroy(highestBlockAt);
            highestBlockAt = highestBlockAt.getRelative(BlockFace.DOWN);
        }
    }

    private void fancyDestroy(final Block block)
    {
        final World world = block.getWorld();

        final Location location = block.getLocation();
        final NorthFallingBlock northFallingBlock = NorthFallingBlock.createNormal(location, block.getType(), block.getData());
        final Vector vel = this.calcVector(this.arenaCenter, block).normalize().multiply(DioriteRandomUtils.nextDouble());

        northFallingBlock.getBukkitEntity().setVelocity(vel);
        ((CraftWorld) world).addEntity(northFallingBlock, CreatureSpawnEvent.SpawnReason.CUSTOM);

        //world.spawnParticle(Particle.BLOCK_CRACK, location, 1, block.getType().getNewData(block.getData()));

        FastBlockOp.setType(block, Material.AIR, (byte) 0);
    }

    private Vector calcVector(final Location center, final Block block)
    {
        final double dX = center.getX() - block.getX();
        final double dY = center.getY() - block.getY();
        final double dZ = center.getZ() - block.getZ();

        final double yaw = Math.atan2(dZ, dX);
        final double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;

        final double X = Math.sin(pitch) * Math.cos(yaw);
        final double Y = Math.sin(pitch) * Math.sin(yaw);
        final double Z = Math.cos(pitch);

        return new Vector(X, Z, Y);
    }
}
