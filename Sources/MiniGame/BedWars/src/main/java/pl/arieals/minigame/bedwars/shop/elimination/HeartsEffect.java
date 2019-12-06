package pl.arieals.minigame.bedwars.shop.elimination;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;

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

        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            return;
        }

        arena.getScheduler().runSimpleCountdown(new SimpleCountdown(20).tickCallback(() ->
        {
            location.getWorld().spawnParticle(Particle.HEART, location, 2, 1, 1, 1);
        }));
    }
}
