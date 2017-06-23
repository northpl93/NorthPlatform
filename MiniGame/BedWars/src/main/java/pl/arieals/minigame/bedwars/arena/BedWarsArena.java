package pl.arieals.minigame.bedwars.arena;

import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import java.util.ArrayList;
import java.util.List;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.cfg.BedWarsArenaConfig;
import pl.arieals.minigame.bedwars.cfg.BedWarsGenerator;
import pl.arieals.minigame.bedwars.cfg.BedWarsGeneratorType;

public class BedWarsArena implements IArenaData
{
    private final BedWarsArenaConfig  config;
    private List<GeneratorController> generators = new ArrayList<>();
    private List<Team>                teams      = new ArrayList<>();

    public BedWarsArena(final LocalArena arena, final BedWarsArenaConfig config)
    {
        this.config = config;
        for (final BedWarsGenerator generatorConfig : config.getGenerators())
        {
            this.generators.add(new GeneratorController(arena, this, generatorConfig));
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

    public List<GeneratorController> getGenerators()
    {
        return this.generators;
    }
}
