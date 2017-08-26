package pl.arieals.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class HeartsEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "hearts";
    }

    @Override
    public void play(final Player player)
    {
        final Location location = player.getLocation();
        this.calculateLegsLocation(location);

        player.getWorld().spawnParticle(Particle.HEART, location, 10);
    }
}
