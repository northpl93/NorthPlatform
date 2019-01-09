package pl.arieals.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class SmokeEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "smoke";
    }

    @Override
    public void play(final Player player)
    {
        final Location location = player.getLocation();
        this.calculateLegsLocation(location);

        location.getWorld().spawnParticle(Particle.FOOTSTEP, location, 5, 0.1, 0.1, 0.1);
    }
}
