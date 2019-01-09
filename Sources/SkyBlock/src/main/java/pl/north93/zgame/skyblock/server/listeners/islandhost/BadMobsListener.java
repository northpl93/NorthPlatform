package pl.north93.zgame.skyblock.server.listeners.islandhost;


import java.util.Arrays;
import java.util.List;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import static org.bukkit.entity.EntityType.*;

public class BadMobsListener implements Listener
{
    private static final List<EntityType> BAD_TYPES = Arrays.asList(WITHER, GHAST, BLAZE, ENDER_DRAGON);
    private static final List<EntityType> DUMB_ANIMALS = Arrays.asList(CHICKEN, PIG, SHEEP, COW);

    @EventHandler
    public void onBadMobSpawn(final CreatureSpawnEvent event)
    {
        final SpawnReason spawnReason = event.getSpawnReason();

        //dumbness for random animals spawned
        if(DUMB_ANIMALS.contains(event.getEntityType()))
        {
            boolean canSpawnDumb = false;

            if (spawnReason.equals(SpawnReason.BREEDING))
            {
                canSpawnDumb = true;
            }
            else if (spawnReason.equals(SpawnReason.EGG))
            {
                //20% chance to spawn a chicken
                if(Math.random() < 0.8)
                {
                    event.setCancelled(true);
                    return;
                }

                //if spawns, it still can be dumb
                canSpawnDumb = true;
            }

            if(canSpawnDumb && Math.random() > 0.9)
            {
                ((CraftLivingEntity) event.getEntity()).getHandle().fromMobSpawner = true;
            }
        }


        if (spawnReason.equals(SpawnReason.SPAWNER_EGG))
        {
            event.getEntity().setRemoveWhenFarAway(false);
            return; //will allow spawn egg to spawn bad mobs
        }

        //bad mobs checker
        if (BAD_TYPES.contains(event.getEntityType()))
        {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onEndermanGrab(final EntityChangeBlockEvent event)
    {
        if (event.getEntityType().equals(ENDERMAN))
        {
            event.setCancelled(true);
        }
    }
}
