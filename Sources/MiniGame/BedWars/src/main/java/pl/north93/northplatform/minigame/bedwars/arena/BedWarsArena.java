package pl.north93.northplatform.minigame.bedwars.arena;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.block.Block;

import org.apache.commons.lang3.tuple.Pair;

import pl.north93.northplatform.api.bukkit.utils.region.Cuboid;
import pl.north93.northplatform.api.bukkit.utils.xml.XmlCuboid;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.IArenaData;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.arena.generator.GeneratorController;
import pl.north93.northplatform.minigame.bedwars.cfg.BwArenaConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGenerator;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGeneratorItemConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGeneratorType;
import pl.north93.northplatform.minigame.bedwars.cfg.BwTeamConfig;

public class BedWarsArena implements IArenaData
{
    private final LocalArena arena;
    private final BwConfig bedWarsConfig;
    private final BwArenaConfig config;
    private final Set<BwGeneratorItemConfig> announcedItems = new HashSet<>();
    private final Set<GeneratorController> generators = new HashSet<>();
    private final Set<Team> teams = new HashSet<>();
    private final Set<Cuboid> secureRegions = new HashSet<>();
    private final Set<Block> playerBlocks = new HashSet<>();
    private final Set<BedWarsPlayer> players = new HashSet<>();

    public BedWarsArena(final LocalArena arena, final BwConfig bedWarsConfig, final BwArenaConfig config)
    {
        this.arena = arena;
        this.bedWarsConfig = bedWarsConfig;
        this.config = config;
        for (final BwGenerator generatorConfig : config.getGenerators())
        {
            this.generators.add(new GeneratorController(arena, bedWarsConfig, this, generatorConfig));
        }
        for (final BwTeamConfig teamConfig : config.getTeams())
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

    public BwArenaConfig getConfig() // config tej konkretnej areny
    {
        return this.config;
    }

    public BwConfig getBedWarsConfig() // globalny config bedwarsów
    {
        return this.bedWarsConfig;
    }

    public Set<BwGeneratorItemConfig> getAnnouncedItems()
    {
        return this.announcedItems;
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

    public Set<BedWarsPlayer> getPlayers()
    {
        return this.players;
    }

    public Set<Cuboid> getSecureRegions()
    {
        return this.secureRegions;
    }

    public Set<Block> getPlayerBlocks()
    {
        return this.playerBlocks;
    }

    public Pair<BwGeneratorType, BwGeneratorItemConfig> nextUpgrade()
    {
        final long time = this.arena.getTimer().getCurrentTime(TimeUnit.SECONDS) * 20;
        return this.bedWarsConfig.getGeneratorTypes()
                                 .stream()
                                 .flatMap(type -> type.getItems().stream().map(item -> Pair.of(type, item)))
                                 .filter(pair -> pair.getValue().getStartAt() > time)
                                 .min(Comparator.comparingInt(p -> p.getValue().getStartAt()))
                                 .orElse(null);
    }
}
