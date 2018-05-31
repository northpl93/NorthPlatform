package pl.arieals.api.minigame.shared.impl.status;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatusManager;
import pl.arieals.api.minigame.shared.api.status.OfflineStatus;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;

/*default*/ class PlayerStatusManagerImpl implements IPlayerStatusManager
{
    private static final MetaKey PLAYER_STATUS_KEY = MetaKey.get("playerNetworkStatus", false);
    @Inject
    private INetworkManager networkManager;

    @Bean
    private PlayerStatusManagerImpl()
    {
    }

    @Override
    public IPlayerStatus getPlayerStatus(final Identity identity)
    {
        final Optional<IPlayer> player = this.networkManager.getPlayers().unsafe().get(identity);
        if (! player.isPresent())
        {
            return OfflineStatus.INSTANCE;
        }

        final MetaStore metaStore = player.get().getMetaStore();
        if (metaStore.contains(PLAYER_STATUS_KEY))
        {
            return metaStore.get(PLAYER_STATUS_KEY);
        }

        return OfflineStatus.INSTANCE;
    }

    @Override
    public void updatePlayerStatus(final Identity identity, final IPlayerStatus newStatus)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            if (t.isOffline())
            {
                return;
            }

            t.getPlayer().getMetaStore().set(PLAYER_STATUS_KEY, newStatus);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
