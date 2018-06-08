package pl.mcpiraci.world.properties.impl;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class WorldPropertiesComponent extends Component
{
    @Inject
    private PropertiesManagerImpl propertiesManager;
    
    @Override
    protected void enableComponent()
    {   
        propertiesManager.reloadServerConfig();
    }

    @Override
    protected void disableComponent()
    {
    }
    
}
