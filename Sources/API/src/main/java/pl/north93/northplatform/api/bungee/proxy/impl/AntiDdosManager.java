package pl.north93.northplatform.api.bungee.proxy.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.ToString;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.northplatform.api.bungee.BungeeApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.config.ConfigUpdatedNetEvent;
import pl.north93.northplatform.api.global.config.IConfig;
import pl.north93.northplatform.api.global.config.NetConfig;
import pl.north93.northplatform.api.global.network.proxy.AntiDdosConfig;
import pl.north93.northplatform.api.global.network.proxy.AntiDdosMode;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;

@ToString
public class AntiDdosManager implements Listener
{
    private static final int ANTI_DDOS_CYCLE_TIME = 20;
    @Inject
    @NetConfig(type = AntiDdosConfig.class, id = "antiddos")
    private IConfig<AntiDdosConfig> config;
    @Inject
    private AntiDdosState antiDdosState;
    private final AtomicInteger connections;
    private Instant suspendedUntil;

    @Bean
    private AntiDdosManager(final BungeeApiCore apiCore)
    {
        apiCore.registerListeners(this);
        apiCore.getPlatformConnector().runTaskAsynchronously(this::checkAuto, ANTI_DDOS_CYCLE_TIME);

        this.connections = new AtomicInteger(0);
        this.resetSuspendState();
    }

    // metoda resetujaca licznik polaczen i automatycznie zmieniajaca stan anty-ddosa w trybie auto
    private void checkAuto()
    {
        final AntiDdosConfig config = this.config.get();
        final int connectionsCount = this.getAndResetState();

        // sprawdzamy czy powinnismy pominac cykl automatycznego anty-ddosu
        if (this.shouldSkipUpdateAntiDdosState(config))
        {
            return;
        }

        if (connectionsCount >= config.getConnectionsThreshold())
        {
            this.antiDdosState.setState(true);

            final Duration duration = Duration.ofSeconds(config.getDuration());
            this.suspendedUntil = Instant.now().plus(duration);
        }
        else
        {
            this.antiDdosState.setState(false);
        }
    }

    @NetEventSubscriber(ConfigUpdatedNetEvent.class)
    public void onAntiDdosConfigReloaded(final ConfigUpdatedNetEvent event)
    {
        if (! event.getConfigName().equals("antiddos"))
        {
            return;
        }

        final AntiDdosConfig config = this.config.get();
        if (config == null || config.getMode() == AntiDdosMode.OFF)
        {
            this.antiDdosState.setState(false);
        }
        else if (config.getMode() == AntiDdosMode.ON)
        {
            this.antiDdosState.setState(true);
        }

        // po zmienia trybu chcemy zeby ewentualna automatyczna decyzja zaszla natychmiastowo
        this.resetSuspendState();
    }

    @EventHandler
    public void onPlayerPreLogin(final PreLoginEvent event)
    {
        // przy wchodzeniu gracza podbijamy licznik o jeden
        this.connections.incrementAndGet();
    }

    private int getAndResetState()
    {
        return this.connections.getAndSet(0);
    }

    private boolean shouldSkipUpdateAntiDdosState(final AntiDdosConfig config)
    {
        if (config == null || config.getMode() != AntiDdosMode.AUTO)
        {
            return true;
        }

        return Instant.now().isBefore(this.suspendedUntil);
    }

    // resetuje czas przez który nie beda podejmowane automatyczne decyzje
    private void resetSuspendState()
    {
        this.suspendedUntil = Instant.now();
    }
}
