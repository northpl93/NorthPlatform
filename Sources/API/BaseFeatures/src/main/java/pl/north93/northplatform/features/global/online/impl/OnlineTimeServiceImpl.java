package pl.north93.northplatform.features.global.online.impl;

import java.time.Duration;
import java.time.Instant;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.players.PlayerNotFoundException;
import pl.north93.northplatform.features.global.online.IOnlineTimeService;

public class OnlineTimeServiceImpl implements IOnlineTimeService
{
    private static MetaKey CONNECTED_AT = MetaKey.get("connectedAt");
    private static MetaKey TOTAL_ONLINE = MetaKey.get("totalOnlineTime");
    private final IPlayersManager playersManager;

    @Bean
    private OnlineTimeServiceImpl(final IPlayersManager playersManager)
    {
        this.playersManager = playersManager;
    }

    @Override
    public Duration getCurrentOnlineTime(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            final MetaStore onlineMetaStore = t.getPlayer().getOnlineMetaStore();
            if (onlineMetaStore.contains(CONNECTED_AT))
            {
                final Instant connectTime = onlineMetaStore.getInstant(CONNECTED_AT);
                return Duration.between(connectTime, Instant.now());
            }

            return Duration.ZERO;
        }
        catch (final PlayerNotFoundException e)
        {
            return Duration.ZERO;
        }
    }

    @Override
    public Duration getTotalOnlineTime(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            final MetaStore metaStore = t.getPlayer().getMetaStore();
            if (metaStore.contains(TOTAL_ONLINE))
            {
                return metaStore.getDuration(TOTAL_ONLINE);
            }

            return Duration.ZERO;
        }
        catch (final PlayerNotFoundException e)
        {
            return Duration.ZERO;
        }
    }

    @Override
    public void resetTotalOnlineTime(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            final MetaStore metaStore = t.getPlayer().getMetaStore();
            metaStore.remove(TOTAL_ONLINE);
        }
    }

    public void startTrackingOnlineTime(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            final MetaStore onlineMetaStore = t.getPlayer().getOnlineMetaStore();
            onlineMetaStore.setInstant(CONNECTED_AT, Instant.now());
        }
    }

    public void endTrackingOnlineTime(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            final IPlayer player = t.getPlayer();

            final MetaStore onlineMetaStore = player.getOnlineMetaStore();
            if (! onlineMetaStore.contains(CONNECTED_AT))
            {
                return;
            }

            final Instant connectedAt = onlineMetaStore.getInstant(CONNECTED_AT);
            final Duration onlineTime = Duration.between(connectedAt, Instant.now());

            final MetaStore metaStore = player.getMetaStore();
            if (metaStore.contains(TOTAL_ONLINE))
            {
                final Duration currentTotalOnline = metaStore.getDuration(TOTAL_ONLINE);
                metaStore.setDuration(TOTAL_ONLINE, currentTotalOnline.plus(onlineTime));
            }
            else
            {
                metaStore.setDuration(TOTAL_ONLINE, onlineTime);
            }
        }
    }
}
