package pl.arieals.minigame.bedwars.arena.upgrade;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorController;

public class BaseGeneratorUpgrade implements IUpgrade
{
    public static final BaseGeneratorUpgrade INSTANCE = new BaseGeneratorUpgrade();

    @Override
    public void apply(final LocalArena arena, final Team team)
    {
        final BedWarsArena arenaData = arena.getArenaData();
        final GeneratorController generator = this.findGenerator(arenaData, team);

        for (final GeneratorController.ItemGeneratorEntry entry : generator.getEntries())
        {
            entry.speedup(from -> from / 2);
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
