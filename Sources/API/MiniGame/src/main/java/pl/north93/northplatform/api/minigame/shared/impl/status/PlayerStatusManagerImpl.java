package pl.north93.northplatform.api.minigame.shared.impl.status;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatusManager;
import pl.north93.northplatform.api.minigame.shared.api.status.OfflineStatus;

/*default*/ class PlayerStatusManagerImpl implements IPlayerStatusManager
{
    private static final MetaKey PLAYER_STATUS_KEY = MetaKey.get("playerNetworkStatus");
    @Inject
    private IPlayersManager playersManager;

    @Bean
    private PlayerStatusManagerImpl()
    {
    }

    @Override
    public IPlayerStatus getPlayerStatus(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            if (t.isOffline())
            {
                return OfflineStatus.INSTANCE;
            }

            final MetaStore metaStore = t.<IOnlinePlayer>getPlayer().getOnlineMetaStore();
            if (metaStore.contains(PLAYER_STATUS_KEY))
            {
                return metaStore.get(PLAYER_STATUS_KEY);
            }

            return OfflineStatus.INSTANCE;
        }
    }

    @Override
    public void updatePlayerStatus(final Identity identity, final IPlayerStatus newStatus)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            if (t.isOffline())
            {
                return;
            }

            final IOnlinePlayer onlinePlayer = t.getPlayer();
            onlinePlayer.getOnlineMetaStore().set(PLAYER_STATUS_KEY, newStatus);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
