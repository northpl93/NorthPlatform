package pl.north93.northplatform.api.test;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.standalone.StandaloneHostConnector;
import pl.north93.northplatform.api.standalone.cfg.EnvironmentCfg;

@Slf4j
public class TestHostConnector extends StandaloneHostConnector
{
    @Override
    public String onPlatformInit(final ApiCore apiCore)
    {
        log.info("Initialising testing platform");

        this.environmentCfg = new EnvironmentCfg("testenv");
        return this.environmentCfg.getId();
    }
}
