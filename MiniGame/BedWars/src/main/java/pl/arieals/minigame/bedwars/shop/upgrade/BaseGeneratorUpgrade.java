package pl.arieals.minigame.bedwars.shop.upgrade;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorController;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class BaseGeneratorUpgrade implements IUpgrade
{
    @Inject
    private BwConfig config;

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        final BedWarsArena arenaData = arena.getArenaData();
        final GeneratorController generator = this.findGenerator(arenaData, team);

        for (final GeneratorController.ItemGeneratorEntry entry : generator.getEntries())
        {
            entry.speedup(from -> from + 1);
        }

        if (this.config.getTeamSize() == 4)
        {
            // todo squad - emeraldy
        }
    }

    @Override
    public int maxLevel()
    {
        return 2;
    }

    private GeneratorController findGenerator(final BedWarsArena arena, final Team team)
    {
        for (final GeneratorController generator : arena.getGenerators())
        {
            if (team.getTeamArena().contains(generator.getLocation()))
            {
                return generator;
            }
        }

        throw new RuntimeException("Can't find team's generator");
    }
}
