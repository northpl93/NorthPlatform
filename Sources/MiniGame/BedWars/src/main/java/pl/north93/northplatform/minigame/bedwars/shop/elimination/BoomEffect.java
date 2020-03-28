package pl.north93.northplatform.minigame.bedwars.shop.elimination;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class BoomEffect implements IEliminationEffect
{
    @Override
    public String getName()
    {
        return "boom";
    }

    @Override
    public void playerEliminated(final LocalArena arena, final INorthPlayer player, final INorthPlayer by)
    {
        final Location location = player.getLocation();
        final World world = location.getWorld();

        world.spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }
}
