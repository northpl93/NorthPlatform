package pl.arieals.api.minigame.shared.api;

import java.io.File;

import pl.arieals.api.minigame.shared.api.cfg.GameMapConfig;

public class MapTemplate
{
    private final String name;
    
    private final File mapDirectory;
    
    private final GameMapConfig mapConfig;
    
    public MapTemplate(String name, File mapDirectory, GameMapConfig mapConfig)
    {
        this.name = name;
        this.mapDirectory = mapDirectory;
        this.mapConfig = mapConfig;
    }
    
    public String getName()
    {
        return name;
    }
    
    public File getMapDirectory()
    {
        return mapDirectory;
    }
    
    public GameMapConfig getMapConfig()
    {
        return mapConfig;
    }

    public String getDisplayName()
    {
        return mapConfig.getDisplayName() != null ? mapConfig.getDisplayName() : name;
    }
}
