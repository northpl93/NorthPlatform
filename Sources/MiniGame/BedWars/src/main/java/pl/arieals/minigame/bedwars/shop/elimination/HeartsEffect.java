package pl.arieals.minigame.bedwars.shop.elimination;

import org.bukkit.Location;
import org.bukkit.Particle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class HeartsEffect implements IEliminationEffect
{
    @Override
    public String getName()
    {
        return "hearts";
    }

    @Override
    public void playerEliminated(final LocalArena arena, final INorthPlayer player, final INorthPlayer by)
    {
        final Location location = player.getLocation();

        arena.getScheduler().runSimpleCountdown(new SimpleCountdown(20).tickCallback(() ->
        {
            location.getWorld().spawnParticle(Particle.HEART, location, 2, 1, 1, 1);
        }));
    }
}
