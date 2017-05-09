package pl.arieals.api.minigame.server.gamehost.world.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.spigotmc.SneakyThrow;

import com.google.common.base.Preconditions;

import pl.arieals.api.minigame.server.gamehost.world.IMapTemplateManager;
import pl.arieals.api.minigame.shared.api.GameMapConfig;
import pl.arieals.api.minigame.shared.api.MapTemplate;

public class MapTemplateManager implements IMapTemplateManager
{
    private final Map<String, MapTemplate> loadedTemplates = new HashMap<>();
    
    private File templateDirectory;

    @Override
    public MapTemplate getMapTemplate(String mapName)
    {
        return loadedTemplates.get(mapName);
    }

    @Override
    public void addMapTemplate(MapTemplate mapTemplate)
    {
        Preconditions.checkState(!loadedTemplates.containsKey(mapTemplate));
        loadedTemplates.put(mapTemplate.getName(), mapTemplate);
    }

    @Override
    public void removeMapTemplate(String mapName)
    {
        loadedTemplates.remove(mapName);
    }

    @Override
    public List<MapTemplate> getAllTemplates()
    {
        return new ArrayList<>(loadedTemplates.values());
    }

    @Override
    public File getTemplatesDirectory()
    {
        return templateDirectory;
    }

    @Override
    public void setTemplatesDirectory(File templatesDirectory)
    {
        this.templateDirectory = templatesDirectory;    
    }
    
    @Override
    public void loadTemplatesFromDirectory() throws Exception
    {
        Preconditions.checkNotNull(templateDirectory);
        Preconditions.checkState(templateDirectory.exists() && templateDirectory.isDirectory(), "Couldn't load map templates from (" + templateDirectory.getAbsolutePath() + ") directory doesn't exists or is file");
        loadedTemplates.clear();
        
        for ( File mapFolder : templateDirectory.listFiles() )
        {
            if ( mapFolder.isFile() )
            {
                continue;
            }
            
            File config = new File(mapFolder, "mapconfig.xml");
            if ( !config.exists() || !config.isFile() )
            {
                continue;
            }
            
            GameMapConfig mapConfig = loadMapConfig(config);
            if ( !mapConfig.isEnabled() )
            {
                continue;
            }
            
            MapTemplate template = new MapTemplate(mapFolder.getName(), mapFolder, mapConfig);
            addMapTemplate(template);
        }
    }
    
    private GameMapConfig loadMapConfig(File configFile) throws Exception
    {
        JAXBContext ctx = JAXBContext.newInstance(GameMapConfig.class);
        return (GameMapConfig) ctx.createUnmarshaller().unmarshal(configFile);
    }
}

