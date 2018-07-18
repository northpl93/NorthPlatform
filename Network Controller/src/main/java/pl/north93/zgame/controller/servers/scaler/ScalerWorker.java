package pl.north93.zgame.controller.servers.scaler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.event.NetworkShutdownNetEvent;
import pl.north93.zgame.api.global.redis.event.NetEventSubscriber;
import pl.north93.zgame.controller.servers.groups.ILocalServersGroup;
import pl.north93.zgame.controller.servers.groups.LocalGroupsManager;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;

public class ScalerWorker implements Runnable
{
    private final Logger logger = LoggerFactory.getLogger(ScalerWorker.class);
    @Inject
    private LocalGroupsManager localGroupsManager;
    @Inject
    private RulesProcessor     rulesProcessor;
    private boolean            isStopping;

    @Bean
    private ScalerWorker(final ApiCore apiCore)
    {
        apiCore.getPlatformConnector().runTaskAsynchronously(this, 20);
    }

    @Override
    public void run()
    {
        if (this.isStopping)
        {
            return;
        }

        for (final ILocalServersGroup group : this.localGroupsManager.getLocalGroups())
        {
            if (group instanceof LocalManagedServersGroup)
            {
                this.processGroup((LocalManagedServersGroup) group);
            }
        }
    }

    private void processGroup(final LocalManagedServersGroup group)
    {
        try
        {
            // aktualizuje stan listy operacji, miedzy innymi usuwa z listy zakonczone operacje
            group.getOperations();

            // jesli grupa ma wylaczone aktualnie automatyczne wydawanie decyzji to nic dalej nie robimy
            if (! group.shouldBeTicked())
            {
                return;
            }

            // przetwarza liste zasad i generuje decyzje, nastepnie ja przekazuje do realizacji
            this.rulesProcessor.generateDecisionAndApply(group);
        }
        catch (final Exception e)
        {
            this.logger.error("An exception has been throw while processing group {}", group.getName(), e);
        }
    }

    @NetEventSubscriber(NetworkShutdownNetEvent.class)
    public void onNetShutdownEvent(final NetworkShutdownNetEvent event) // nasluchuje na event wylaczenia sieci
    {
        this.logger.info("ScalerWorker will no longer generate decisions in result of network shutdown.");
        this.isStopping = true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
