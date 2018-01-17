package pl.arieals.minigame.bedwars.shop.elimination;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.Vector3f;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.utils.SimpleCountdown;
import pl.north93.zgame.api.bukkit.utils.nms.NorthFallingBlock;

public class GraveEffect implements IEliminationEffect
{
    private static final int GRAVE_REMOVE = 20 * 5;

    @Override
    public String getName()
    {
        return "grave";
    }

    @Override
    public void playerEliminated(final Player player, final Player by)
    {
        final Location location = player.getLocation();
        final CraftWorld world = (CraftWorld) location.getWorld();

        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            return;
        }

        final Collection<Entity> entities = new ArrayList<>();

        final Block block = location.getBlock();
        block.getLocation(location);

        final Material type = block.getType();
        if (type == Material.STONE_SLAB2 || type == Material.STEP)
        {
            location.add(0, 0.5, 0);
        }

        {
            final NorthFallingBlock b1 = NorthFallingBlock.createDerped(location, Material.STEP, (byte) 3);
            b1.setLocation(location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5, 0, 0);
            world.addEntity(b1, CreatureSpawnEvent.SpawnReason.CUSTOM);
            entities.add(b1.getBukkitEntity());
        }

        {
            final Location armorStandLocation = location.add(-0.2, -0.2, 0.8);

            final EntityArmorStand entityArmorStand = (EntityArmorStand) world.createEntity(armorStandLocation, ArmorStand.class);
            final ArmorStand armorStand = (ArmorStand) entityArmorStand.getBukkitEntity();

            entityArmorStand.setHeadPose(new Vector3f(0, 0, 120));
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setHelmet(new ItemStack(Material.DIAMOND_SWORD));
            armorStand.setInvulnerable(true);

            world.addEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);

            entities.add(armorStand);
        }

        entities.addAll(IEliminationEffect.dropItems(player.getEyeLocation(), new ItemStack(Material.BONE), 8));

        final Location particle = block.getLocation().add(0.5, 0, 0.5);
        arena.getScheduler().runSimpleCountdown(new SimpleCountdown(20).tickCallback(() ->
        {
            world.spawnParticle(Particle.EXPLOSION_LARGE, particle, 2, 0.2, 0.2, 0.2);
        }));

        arena.getScheduler().runTaskLater(() ->
        {
            for (final Entity entity : entities)
            {
                if (entity.isDead())
                {
                    continue;
                }

                entity.remove();

                // force block update
                final byte data = block.getData();
                block.setType(type);
                block.setData(data);
            }
        }, GRAVE_REMOVE);
    }
}
