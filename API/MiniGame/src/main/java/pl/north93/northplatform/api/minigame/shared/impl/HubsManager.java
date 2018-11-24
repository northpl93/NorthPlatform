package pl.north93.northplatform.api.minigame.shared.impl;

import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.api.hub.RemoteHub;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.observable.Hash;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;

public class HubsManager
{
    @Inject
    private IObservationManager observer;
    private Hash<RemoteHub>     hubs;

    @Bean
    private HubsManager()
    {
        this.hubs = this.observer.getHash(RemoteHub.class, "hubs");
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("hubs", this.hubs).toString();
    }
}
