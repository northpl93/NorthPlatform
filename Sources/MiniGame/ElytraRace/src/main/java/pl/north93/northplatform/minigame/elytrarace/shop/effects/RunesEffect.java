package pl.north93.northplatform.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class RunesEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "runes";
    }

    @Override
    public void play(final Player player)
    {
        final Location location = player.getLocation();
        this.calculateLegsLocation(location);

        final Location loc1 = player.getLocation();
        loc1.setYaw(loc1.getYaw() + 100);
        this.calculateLegsLocation(loc1, 3);

        final Location loc2 = player.getLocation();
        loc2.setYaw(loc2.getYaw() - 100);
        this.calculateLegsLocation(loc2, 3);

        final World world = player.getWorld();

        world.spawnParticle(Particle.ENCHANTMENT_TABLE, loc1, 25);
        world.spawnParticle(Particle.ENCHANTMENT_TABLE, loc2, 25);
    }
}
