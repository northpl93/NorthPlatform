package pl.north93.zgame.features.controller.punishment;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.controller.configserver.IConfigServer;
import pl.north93.zgame.controller.configserver.source.XmlConfigSource;
import pl.north93.zgame.features.global.punishment.cfg.PunishmentCfg;

public class PunishmentConfigService
{
    @Inject
    private ApiCore       apiCore;
    @Inject
    private IConfigServer configServer;

    @Bean
    private PunishmentConfigService()
    {
        final File punishmentFile = this.apiCore.getFile("Punishment.xml");
        this.configServer.addConfig("punishment", new XmlConfigSource<>(PunishmentCfg.class, punishmentFile));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("configServer", this.configServer).toString();
    }
}
