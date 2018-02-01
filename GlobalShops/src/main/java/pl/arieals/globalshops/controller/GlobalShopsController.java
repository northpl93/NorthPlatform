package pl.arieals.globalshops.controller;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.controller.configserver.IConfigServer;

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
