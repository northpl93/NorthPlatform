package pl.north93.northplatform.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class WaterEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "water";
    }

    @Override
    public void play(final Player player)
    {
        final Location location = player.getLocation();
        this.calculateLegsLocation(location);

        player.getWorld().spawnParticle(Particle.WATER_BUBBLE, location, 50, 0.6, 0.6, 0.6);

        /*for (int i = 0; i < 10; i++)
        {
            player.getWorld().spawnParticle(Particle.WATER_BUBBLE, location, 1, 0.5, 0.5, 0.5);
        }*/
    }
}
