package pl.north93.northplatform.api.minigame.controller.booster;

import java.io.File;

import pl.north93.northplatform.api.minigame.shared.impl.booster.cfg.BoostersCfg;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.controller.configserver.IConfigServer;
import pl.north93.northplatform.controller.configserver.source.XmlConfigSource;

public class BoosterConfigRegistrar
{
    @Bean
    private BoosterConfigRegistrar(final ApiCore apiCore, final IConfigServer configServer)
    {
        final File file = apiCore.getFile("boosters.xml");
        configServer.addConfig("boosters", new XmlConfigSource<>(BoostersCfg.class, file));
    }
}
