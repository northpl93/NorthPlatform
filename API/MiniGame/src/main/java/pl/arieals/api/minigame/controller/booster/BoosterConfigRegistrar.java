package pl.arieals.api.minigame.controller.booster;

import java.io.File;

import pl.arieals.api.minigame.shared.impl.booster.cfg.BoostersCfg;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.controller.configserver.IConfigServer;
import pl.north93.zgame.controller.configserver.source.XmlConfigSource;

public class BoosterConfigRegistrar
{
    @Bean
    private BoosterConfigRegistrar(final ApiCore apiCore, final IConfigServer configServer)
    {
        final File file = apiCore.getFile("boosters.xml");
        configServer.addConfig("boosters", new XmlConfigSource<>(BoostersCfg.class, file));
    }
}
