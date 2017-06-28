package pl.arieals.minigame.bedwars.arena;

import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Block;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorController;
import pl.arieals.minigame.bedwars.cfg.BedWarsArenaConfig;
import pl.arieals.minigame.bedwars.cfg.BedWarsGenerator;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorType;
import pl.arieals.minigame.bedwars.cfg.BedWarsTeamConfig;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;

public class BedWarsArena implements IArenaData
{
    private final BedWarsArenaConfig  config;
    private Set<GeneratorController> generators    = new HashSet<>();
    private Set<Team>                teams         = new HashSet<>();
    private Set<Cuboid>              secureRegions = new HashSet<>();
    private Set<Block>               playerBlocks  = new HashSet<>();

    public BedWarsArena(final LocalArena arena, final BedWarsArenaConfig config)
    {
        this.config = config;
        for (final BedWarsGenerator generatorConfig : config.getGenerators())
        {
            this.generators.add(new GeneratorController(arena, this, generatorConfig));
        }
        for (final BedWarsTeamConfig teamConfig : config.getTeams())
        {
            this.teams.add(new Team(arena, teamConfig));
        }
        for (final XmlCuboid xmlCuboid : config.getSecureRegions())
        {
            this.secureRegions.add(xmlCuboid.toCuboid(arena.getWorld().getCurrentWorld()));
        }
    }

    public BedWarsArenaConfig getConfig()
    {
        return this.config;
    }

    public BedWarsGeneratorType getGeneratorType(final String name)
    {
        return findInCollection(this.config.getGeneratorTypes(), BedWarsGeneratorType::getName, name);
    }

    public Set<GeneratorController> getGenerators()
    {
        return this.generators;
    }

    public Set<Team> getTeams()
    {
        return this.teams;
    }

    public Team getTeamAt(final Block block)
    {
        for (final Team team : this.teams)
        {
            if (team.getTeamArena().contains(block))
            {
                return team;
            }
        }
        return null;
    }

    public Set<Cuboid> getSecureRegions()
    {
        return this.secureRegions;
    }

    public Set<Block> getPlayerBlocks()
    {
        return this.playerBlocks;
    }
}
