package pl.north93.northplatform.api.minigame.server.gamehost.world;

import java.io.File;
import java.util.List;

import pl.north93.northplatform.api.minigame.shared.api.MapTemplate;

public interface IMapTemplateManager
{
    MapTemplate getMapTemplate(String mapName);
    
    void addMapTemplate(MapTemplate mapTemplate);
    
    void removeMapTemplate(String mapName);
    
    List<MapTemplate> getAllTemplates();
    
    void setTemplatesDirectory(File templatesDirectory);
    
    File getTemplatesDirectory();
    
    void loadTemplatesFromDirectory() throws Exception;
    
}
