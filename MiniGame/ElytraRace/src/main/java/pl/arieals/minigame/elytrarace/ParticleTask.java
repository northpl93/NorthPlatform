package pl.arieals.minigame.elytrarace;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArenas;


import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.cfg.Boost;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class ParticleTask implements Runnable
{
    @Override
    public void run()
    {
        for (final LocalArena localArena : getArenas())
        {
            if (localArena.getGamePhase() != GamePhase.STARTED)
            {
                continue;
            }

            final ElytraRaceArena arenaData = localArena.getArenaData();
            for (final Boost boost : arenaData.getArenaConfig().getBoosts())
            {
                final World world = localArena.getWorld().getCurrentWorld();
                final Cuboid blocks = boost.getArea().toCuboid(world);

                if (boost.getBoostType() == BoostType.SPEED)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        final Block randomLoc = blocks.randomBlock();
                        world.spawnParticle(Particle.VILLAGER_HAPPY, randomLoc.getLocation(), 10);
                    }
                }
                else
                {
                    for (int i = 0; i < 6; i++)
                    {
                        final Block randomLoc = blocks.randomBlock();
                        // gdy i=0 i v3!=0 to v/v1/v2 sluza jako rgb podawane jako 0-1. Tutaj zakodowany kolor #F87D23 (248/125/35).
                        world.spawnParticle(Particle.SPELL_MOB, randomLoc.getLocation(), 0, 248d/255d, 125d/255d, 35d/255d, 1);
                    }
                }
            }
        }
    }
}
