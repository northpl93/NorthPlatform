package pl.arieals.minigame.bedwars.shop.elimination;

import static org.bukkit.FireworkEffect.Type;
import static org.bukkit.FireworkEffect.builder;

import static org.diorite.utils.math.DioriteRandomUtils.getRandomInt;


import net.minecraft.server.v1_12_R1.EntityFireworks;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import org.diorite.utils.math.DioriteRandomUtils;

public class FireworkEffect implements IEliminationEffect
{
    @Override
    public String getName()
    {
        return "firework";
    }

    @Override
    public void playerEliminated(final Player player, final Player by)
    {
        final Location location = player.getLocation();
        location.setY(Math.max(0, location.getY()));

        final CraftWorld craftWorld = (CraftWorld) location.getWorld();

        for (int i = 0; i < 3; i++)
        {
            final EntityFireworks entityFireworks = new EntityFireworks(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ(), null);
            final Firework firework = (Firework) entityFireworks.getBukkitEntity();

            final FireworkMeta fireworkMeta = firework.getFireworkMeta();

            final Type type = DioriteRandomUtils.getRandom(Type.values());
            final Color color = Color.fromRGB(getRandomInt(0, 255), getRandomInt(0, 255), getRandomInt(0, 255));

            fireworkMeta.addEffect(builder().with(type).withColor(color).build());
            fireworkMeta.setPower(getRandomInt(1, 5));

            firework.setFireworkMeta(fireworkMeta);
            craftWorld.addEntity(entityFireworks, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }

    }
}
