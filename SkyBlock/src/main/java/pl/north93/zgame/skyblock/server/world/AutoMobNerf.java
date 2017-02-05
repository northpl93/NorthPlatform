package pl.north93.zgame.skyblock.server.world;

import static org.bukkit.entity.EntityType.*;


import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.server.v1_10_R1.EntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.collections.WeakCollection;

public class AutoMobNerf implements Runnable
{
    private static final int    NERF_IN_ONE_WORLD    = 25;
    private static final int    DENERF_IN_CYCLE      = 25;
    private static final double TPS_START_NERFING    = 17.5;
    private static final double TPS_START_DENERFING  = 18.5;
    private final Random random = new Random();
    private final Collection<Entity> nerfedEntities = new WeakCollection<>(256);

    @Override
    public void run()
    {
        final double tps = this.getTps();
        if (tps < TPS_START_NERFING)
        {
            for (final World world : Bukkit.getWorlds())
            {
                int nerfedInWorld = 0;

                for (final Entity entity : world.getEntities())
                {
                    if (entity.isDead() || ! this.canNerf(entity.getType()))
                    {
                        continue;
                    }

                    if (this.random.nextBoolean())
                    {
                        continue;
                    }

                    final EntityLiving nmsLiving = ((CraftLivingEntity) entity).getHandle();
                    if (nmsLiving.fromMobSpawner)
                    {
                        continue;
                    }

                    nmsLiving.fromMobSpawner = true;
                    this.nerfedEntities.add(entity);
                    if (++nerfedInWorld > NERF_IN_ONE_WORLD)
                    {
                        break; // go to next world
                    }
                }
            }
        }
        else if (tps > TPS_START_DENERFING && !this.nerfedEntities.isEmpty())
        {
            int denerfedEntities = 0;
            final Iterator<Entity> iterator = this.nerfedEntities.iterator();
            while (iterator.hasNext())
            {
                final Entity nerfedEntity = iterator.next();
                if (nerfedEntity.isDead())
                {
                    iterator.remove();
                    continue;
                }

                final EntityLiving nmsLiving = ((CraftLivingEntity) nerfedEntity).getHandle();
                nmsLiving.fromMobSpawner = false;
                iterator.remove();
                if (++denerfedEntities > DENERF_IN_CYCLE)
                {
                    break; // stop denerfing
                }
            }
        }
    }

    private boolean canNerf(final EntityType entityType)
    {
        return entityType == CHICKEN || entityType == SHEEP || entityType == COW || entityType == PIG;
    }

    private double getTps()
    {
        return ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("nerfedEntities", this.nerfedEntities).toString();
    }
}
