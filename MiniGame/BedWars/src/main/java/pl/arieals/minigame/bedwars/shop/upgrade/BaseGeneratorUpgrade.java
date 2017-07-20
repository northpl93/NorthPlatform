package pl.arieals.minigame.bedwars.shop.upgrade;

import static org.bukkit.Material.EMERALD;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorController;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.arieals.minigame.bedwars.cfg.BwGeneratorItemConfig;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class BaseGeneratorUpgrade implements IUpgrade
{
    private static final BwGeneratorItemConfig emeralds1 = new BwGeneratorItemConfig("emeralds", EMERALD, (byte) 0, 1, 30, 0);
    private static final BwGeneratorItemConfig emeralds2 = new BwGeneratorItemConfig("emeralds", EMERALD, (byte) 0, 2, 30, 1);
    @Inject
    private BwConfig config;

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        final BedWarsArena arenaData = arena.getArenaData();
        final GeneratorController generator = this.findGenerator(arenaData, team);

        if (this.config.getTeamSize() == 4)
        {
            // ulepszenia dla squad
            if (level == 1)
            {
                generator.addNewEntry(emeralds1);
            }
            else
            {
                generator.addNewEntry(emeralds2);
            }
            return;
        }

        // ulepszenia normalne
        for (final GeneratorController.ItemGeneratorEntry entry : generator.getEntries())
        {
            entry.speedup(from -> from + 1);
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
