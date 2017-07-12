package pl.arieals.minigame.bedwars.arena;

import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.block.Block;

import org.apache.commons.lang3.tuple.Pair;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorController;
import pl.arieals.minigame.bedwars.cfg.BedWarsArenaConfig;
import pl.arieals.minigame.bedwars.cfg.BedWarsGenerator;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorItemConfig;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorType;
import pl.arieals.minigame.bedwars.cfg.BedWarsTeamConfig;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;

public class BedWarsArena implements IArenaData
{
    private final LocalArena         arena;
    private final BedWarsArenaConfig config;
    private final Set<GeneratorController> generators    = new HashSet<>();
    private final Set<Team>                teams         = new HashSet<>();
    private final Set<Cuboid>              secureRegions = new HashSet<>();
    private final Set<Block>               playerBlocks  = new HashSet<>();

    public BedWarsArena(final LocalArena arena, final BedWarsArenaConfig config)
    {
        this.arena = arena;
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

    public LocalArena getArena()
    {
        return this.arena;
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

    public Pair<BedWarsGeneratorType, BedWarsGeneratorItemConfig> nextUpgrade()
    {
        final long time = this.arena.getTimer().getCurrentTime(TimeUnit.SECONDS) * 20;
        return this.config.getGeneratorTypes()
                          .stream()
                          .flatMap(type -> type.getItems().stream().map(item -> Pair.of(type, item)))
                          .filter(pair -> pair.getValue().getStartAt() > time)
                          .sorted(Comparator.comparingInt(p -> p.getValue().getStartAt()))
                          .findFirst().orElse(null);
    }
}
