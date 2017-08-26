package pl.arieals.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MagicEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "magic";
    }

    @Override
    public void play(final Player player)
    {
        final Location loc1 = player.getLocation();
        loc1.setYaw(loc1.getYaw() + 75);
        this.calculateLegsLocation(loc1, 0.5);

        final Location loc2 = player.getLocation();
        loc2.setYaw(loc2.getYaw() - 75);
        this.calculateLegsLocation(loc2, 0.5);

        final World world = player.getWorld();

        world.spawnParticle(Particle.CRIT_MAGIC, loc1, 15);
        world.spawnParticle(Particle.CRIT_MAGIC, loc2, 15);
    }
}
