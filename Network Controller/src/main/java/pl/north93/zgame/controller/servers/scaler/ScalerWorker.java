package pl.north93.zgame.controller.servers.scaler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.controller.servers.groups.ILocalServersGroup;
import pl.north93.zgame.controller.servers.groups.LocalGroupsManager;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;

public class ScalerWorker implements Runnable
{
    @Inject
    private LocalGroupsManager localGroupsManager;
    @Inject
    private RulesProcessor     rulesProcessor;

    @Bean
    private ScalerWorker(final ApiCore apiCore)
    {
        apiCore.getPlatformConnector().runTaskAsynchronously(this, 20);
    }

    @Override
    public void run()
    {
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
        // aktualizuje stan listy operacji
        group.getOperations();

        // jesli grupa ma wylaczone aktualnie automatyczne wydawanie decyzji to nic dalej nie robimy
        if (! group.shouldBeTicked())
        {
            return;
        }

        // przetwarza liste zasad i generuje decyzje, nastepnie ja przekazuje do realizacji
        this.rulesProcessor.generateDecisionAndApply(group);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
