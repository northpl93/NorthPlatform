package pl.north93.northplatform.api.minigame.shared.impl;

import java.util.Set;
import java.util.UUID;

import lombok.ToString;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.redis.observable.Hash;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.minigame.shared.api.hub.RemoteHub;

@ToString
public class HubsManager
{
    private Hash<RemoteHub> hubs;

    @Bean
    private HubsManager(final IObservationManager observer)
    {
        this.hubs = observer.getHash(RemoteHub.class, "hubs");
    }

    public RemoteHub getHub(final UUID serverId)
    {
        return this.hubs.get(serverId.toString());
    }

    public Set<RemoteHub> getAllHubs()
    {
        return this.hubs.values();
    }

    public void setHub(final RemoteHub hub)
    {
        this.hubs.put(hub.getServerId().toString(), hub);
    }

    public void removeHub(final UUID serverId)
    {
        this.hubs.delete(serverId.toString());
    }
}
