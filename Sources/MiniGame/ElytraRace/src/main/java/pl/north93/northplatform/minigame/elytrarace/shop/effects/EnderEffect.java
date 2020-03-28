package pl.north93.northplatform.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class EnderEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "ender";
    }

    @Override
    public void play(final Player player)
    {
        final Location location = player.getLocation();
        this.calculateLegsLocation(location);

        player.getWorld().spawnParticle(Particle.PORTAL, location, 100);
    }
}
