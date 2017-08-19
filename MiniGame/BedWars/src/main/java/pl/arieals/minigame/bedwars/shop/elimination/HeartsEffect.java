package pl.arieals.minigame.bedwars.shop.elimination;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class HeartsEffect implements IEliminationEffect
{
    @Override
    public String getName()
    {
        return "hearts";
    }

    @Override
    public void playerEliminated(final Player player, final Player by)
    {
        final Location location = player.getLocation();
        location.getWorld().spawnParticle(Particle.HEART, location, 20, 1, 1, 1);
    }
}
