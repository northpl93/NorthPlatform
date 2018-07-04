package pl.north93.zgame.auth.sharedimpl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.auth.api.IAuthManager;
import pl.north93.zgame.auth.api.IAuthPlayer;

/*default*/ class AuthManagerImpl implements IAuthManager
{
    @Inject
    private INetworkManager networkManager;

    @Bean
    private AuthManagerImpl()
    {
    }

    @Override
    public IAuthPlayer getPlayer(final Identity identity)
    {
        return new AuthPlayerImpl(this.networkManager, identity);
    }

    @Override
    public boolean isLoggedIn(final String name)
    {
        final IOnlinePlayer player = this.networkManager.getPlayers().unsafe().getOnlineValue(name).get();
        if (player == null)
        {
            return false;
        }
        else if (player.isPremium())
        {
            return true;
        }

        final MetaStore metaStore = player.getMetaStore();
        if (metaStore.contains(IAuthPlayer.LOGGED_IN))
        {
            return metaStore.get(IAuthPlayer.LOGGED_IN);
        }

        return false;
    }

    @Override
    public void setLoggedInStatus(final Identity identity, final boolean status)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            if (t.isOffline())
            {
                return;
            }

            final MetaStore metaStore = t.getPlayer().getMetaStore();
            metaStore.set(IAuthPlayer.LOGGED_IN, status);
        }
    }

    @Override
    public void deleteStatus(final Identity identity)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            if (t.isOffline())
            {
                return;
            }

            final MetaStore metaStore = t.getPlayer().getMetaStore();
            metaStore.remove(IAuthPlayer.LOGGED_IN);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
