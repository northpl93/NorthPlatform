package pl.north93.northplatform.minigame.elytrarace;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArenas;


import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRaceArena;
import pl.north93.northplatform.minigame.elytrarace.cfg.Boost;
import pl.north93.northplatform.api.bukkit.utils.region.Cuboid;

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

            // tworzymy tylko jeden obiekt Location dla kazdej areny.
            // Zapobiega to masowemu tworzeniu gigantycznej ilosci tych obiektów
            // co zapychało garbage collector.
            final Location location = new Location(localArena.getWorld().getCurrentWorld(), 0, 0, 0);

            final ElytraRaceArena arenaData = localArena.getArenaData();
            for (final Boost boost : arenaData.getArenaConfig().getBoosts())
            {
                final World world = localArena.getWorld().getCurrentWorld();
                final Cuboid blocks = boost.getArea().toCuboid(world);

                if (boost.getBoostType() == BoostType.SPEED)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        blocks.randomLocation(location);
                        world.spawnParticle(Particle.VILLAGER_HAPPY, location.getX(), location.getY(), location.getZ(), 10);
                    }
                }
                else
                {
                    for (int i = 0; i < 6; i++)
                    {
                        blocks.randomLocation(location);
                        // gdy i=0 i v3!=0 to v/v1/v2 sluza jako rgb podawane jako 0-1. Tutaj zakodowany kolor #F87D23 (248/125/35).
                        world.spawnParticle(Particle.SPELL_MOB,
                                location.getX(), location.getY(), location.getZ(),
                                0, 248d/255d, 125d/255d, 35d/255d, 1);
                    }
                }
            }
        }
    }
}
