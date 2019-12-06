package pl.arieals.minigame.bedwars.shop.elimination;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;

public class BatEffect implements IEliminationEffect
{
    private static final int BAT_KILL = 10 * 20;

    @Override
    public String getName()
    {
        return "bat";
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

        final Bat bat = (Bat) location.getWorld().spawnEntity(location, EntityType.BAT);
        bat.setInvulnerable(true);

        arena.getScheduler().runSimpleCountdown(new SimpleCountdown(20).tickCallback(() ->
        {
            location.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, location, 10);
        }));

        arena.getScheduler().runTaskLater(() ->
        {
            if (! bat.isDead())
            {
                bat.remove();
            }
        }, BAT_KILL);
    }
}
