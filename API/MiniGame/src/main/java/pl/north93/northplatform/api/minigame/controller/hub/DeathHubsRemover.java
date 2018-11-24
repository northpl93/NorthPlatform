package pl.north93.northplatform.api.minigame.controller.hub;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.minigame.shared.api.hub.RemoteHub;
import pl.north93.northplatform.api.minigame.shared.impl.HubsManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.event.ServerDeathNetEvent;
import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;
import pl.north93.northplatform.api.global.network.server.ServerType;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;

/**
 * System obsługujący usuwanie hubów z listy które znajdowały
 * się na serwerach które uległy awarii.
 */
@Slf4j
public class DeathHubsRemover
{
    @Inject
    private HubsManager hubsManager;

    private DeathHubsRemover()
    {
    }

    @NetEventSubscriber(ServerDeathNetEvent.class)
    public void handleDeathServer(final ServerDeathNetEvent event)
    {
        final ServerDto server = event.getServer();
        if (server.getType() == ServerType.MINIGAME)
        {
            // huby znajdują się tylko na serwerach o typie NORMAL
            return;
        }

        final UUID serverUuid = server.getUuid();
        for (final RemoteHub hub : this.hubsManager.getAllHubs())
        {
            if (! hub.getServerId().equals(serverUuid))
            {
                continue;
            }

            this.hubsManager.removeHub(serverUuid);
            log.info("Removed hub due to server {} crash", serverUuid);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
