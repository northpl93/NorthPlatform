package pl.north93.northplatform.minigame.bedwars.shop.elimination;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class BatEffect implements IEliminationEffect
{
    private static final int BAT_KILL = 10 * 20;

    @Override
    public String getName()
    {
        return "bat";
    }

    @Override
    public void playerEliminated(final LocalArena arena, final INorthPlayer player, final INorthPlayer by)
    {
        final Location location = player.getLocation();

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
