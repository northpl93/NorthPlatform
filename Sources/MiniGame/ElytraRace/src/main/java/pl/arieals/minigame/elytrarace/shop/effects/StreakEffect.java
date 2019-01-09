package pl.arieals.minigame.elytrarace.shop.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class StreakEffect implements IElytraEffect
{
    @Override
    public String name()
    {
        return "streak";
    }

    @Override
    public void play(final Player player)
    {
        final Location location = player.getLocation();
        this.calculateLegsLocation(location);

        player.getWorld().spawnParticle(Particle.SPELL_INSTANT, location, 100);
    }
}
