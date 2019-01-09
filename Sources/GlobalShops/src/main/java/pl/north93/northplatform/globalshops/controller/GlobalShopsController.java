package pl.north93.northplatform.globalshops.controller;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.controller.configserver.IConfigServer;

public class GlobalShopsController extends Component
{
    @Inject
    private IConfigServer configServer;

    @Override
    protected void enableComponent()
    {
        this.configServer.addConfig("globalShops", new ShopsConfigSource());
    }

    @Override
    protected void disableComponent()
    {
    	
    }
}
