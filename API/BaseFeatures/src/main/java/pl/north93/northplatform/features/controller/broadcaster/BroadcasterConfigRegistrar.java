package pl.north93.northplatform.features.controller.broadcaster;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.features.controller.broadcaster.cfg.BroadcasterCfg;
import pl.north93.northplatform.controller.configserver.IConfigServer;
import pl.north93.northplatform.controller.configserver.source.XmlConfigSource;

public class BroadcasterConfigRegistrar
{
    @Inject
    private ApiCore       apiCore;
    @Inject
    private IConfigServer configServer;

    @Bean
    private BroadcasterConfigRegistrar()
    {
        final File file = this.apiCore.getFile("broadcaster.xml");
        this.configServer.addConfig("broadcaster", new XmlConfigSource<>(BroadcasterCfg.class, file));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
