package pl.north93.northplatform.minigame.bedwars.shop.elimination;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import org.diorite.commons.math.DioriteRandomUtils;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class BloodEffect implements IEliminationEffect
{
    private static final int ITEMS_REMOVE = 20 * 5;

    @Override
    public String getName()
    {
        return "blood";
    }

    @Override
    public void playerEliminated(final LocalArena arena, final INorthPlayer player, final INorthPlayer by)
    {
        final Location eyeLocation = player.getEyeLocation();

        final MaterialData data = new ItemStack(Material.REDSTONE_BLOCK).getData();
        eyeLocation.getWorld().spawnParticle(Particle.BLOCK_CRACK, eyeLocation, 100, data);

        final int bones = DioriteRandomUtils.getRandomInt(1, 5);
        final Collection<Item> items = IEliminationEffect.dropItems(eyeLocation, new ItemStack(Material.BONE), bones);

        arena.getScheduler().runTaskLater(() ->
        {
            for (final Item item : items)
            {
                if (item.isDead())
                {
                    continue;
                }

                item.remove();
            }
        }, ITEMS_REMOVE);
    }
}
