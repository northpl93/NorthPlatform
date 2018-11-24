package pl.north93.northplatform.auth.sharedimpl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.auth.api.IAuthManager;
import pl.north93.northplatform.auth.api.IAuthPlayer;

/*default*/ class AuthManagerImpl implements IAuthManager
{
    @Inject
    private IPlayersManager playersManager;

    @Bean
    private AuthManagerImpl()
    {
    }

    @Override
    public IAuthPlayer getPlayer(final Identity identity)
    {
        return new AuthPlayerImpl(this.playersManager, identity);
    }

    @Override
    public boolean isLoggedIn(final String name)
    {
        final IOnlinePlayer player = this.playersManager.unsafe().getOnlineValue(name).get();
        if (player == null)
        {
            return false;
        }
        else if (player.isPremium())
        {
            return true;
        }

        final MetaStore metaStore = player.getOnlineMetaStore();
        if (metaStore.contains(IAuthPlayer.LOGGED_IN))
        {
            return metaStore.get(IAuthPlayer.LOGGED_IN);
        }

        return false;
    }

    @Override
    public void setLoggedInStatus(final Identity identity, final boolean status)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            if (t.isOffline())
            {
                return;
            }

            final MetaStore metaStore = t.<IOnlinePlayer>getPlayer().getOnlineMetaStore();
            metaStore.set(IAuthPlayer.LOGGED_IN, status);
        }
    }

    @Override
    public void deleteStatus(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            if (t.isOffline())
            {
                return;
            }

            final MetaStore metaStore = t.<IOnlinePlayer>getPlayer().getOnlineMetaStore();
            metaStore.remove(IAuthPlayer.LOGGED_IN);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
