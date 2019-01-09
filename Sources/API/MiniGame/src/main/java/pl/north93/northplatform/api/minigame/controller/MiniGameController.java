package pl.north93.northplatform.api.minigame.controller;

import java.io.File;

import pl.north93.northplatform.api.minigame.shared.api.cfg.HubsConfig;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.controller.configserver.IConfigServer;
import pl.north93.northplatform.controller.configserver.source.XmlConfigSource;

public class MiniGameController extends Component
{
    @Inject
    private IConfigServer configServer;

    @Override
    protected void enableComponent()
    {
        final File hubsFile = this.getApiCore().getFile("hubs.xml");
        this.configServer.addConfig("hubs", new XmlConfigSource<>(HubsConfig.class, hubsFile));
    }

    @Override
    protected void disableComponent()
    {

    }
}
