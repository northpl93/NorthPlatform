package pl.north93.northplatform.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class RayEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "ray";
    }

    @Override
    public void play(final Player player)
    {
        final Location location = player.getLocation();
        this.calculateLegsLocation(location);

        final MaterialData data = new ItemStack(Material.REDSTONE_BLOCK).getData();
        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, 100, data);
    }
}
