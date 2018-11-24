package pl.north93.northplatform.api.bungee.proxy.impl;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.config.IConfig;
import pl.north93.northplatform.api.global.config.NetConfig;
import pl.north93.northplatform.api.global.network.proxy.AntiDdosConfig;
import pl.north93.northplatform.api.global.network.proxy.AntiDdosExecute;

@Slf4j
@ToString(of = "enabled")
/*default*/ class AntiDdosState
{
    @Inject
    @NetConfig(type = AntiDdosConfig.class, id = "antiddos")
    private IConfig<AntiDdosConfig> config;
    private boolean                 enabled;

    @Bean
    private AntiDdosState()
    {
    }

    /**
     * @return Aktualny stan anty ddosa na tym serwerze proxy.
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * Zmienia aktualny stan anty DDoSa na tym serwerze proxy.
     *
     * @param state Nowy stan anty ddosa.
     */
    public void setState(final boolean state)
    {
        if (this.enabled && ! state)
        {
            this.disable();
        }
        else if (! this.enabled && state)
        {
            this.enable();
        }
        this.enabled = state;
    }

    private void enable()
    {
        log.info("Switching anti-ddos to enabled state");

        final AntiDdosConfig config = this.config.get();
        if (config == null)
        {
            return;
        }

        config.getOnEnable().forEach(this::execute);
    }

    private void disable()
    {
        log.info("Switching anti-ddos to disabled state");

        final AntiDdosConfig config = this.config.get();
        if (config == null)
        {
            return;
        }

        config.getOnDisable().forEach(this::execute);
    }

    private void execute(final AntiDdosExecute execute)
    {
        try
        {
            if (execute.getAsRoot())
            {
                Runtime.getRuntime().exec("sudo " + execute.getCommand());
            }
            else
            {
                Runtime.getRuntime().exec(execute.getCommand());
            }
        }
        catch (final Exception e)
        {
            log.error("Failed to execute {}", execute, e);
        }
    }
}
