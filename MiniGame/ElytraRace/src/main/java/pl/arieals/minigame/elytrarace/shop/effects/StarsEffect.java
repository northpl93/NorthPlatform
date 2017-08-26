package pl.arieals.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class StarsEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "stars";
    }

    @Override
    public void play(final Player player)
    {
        final Location location = player.getLocation();
        this.calculateLegsLocation(location);

        location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 5);
    }
}
