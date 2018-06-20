package pl.north93.zgame.auth.sharedimpl;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.mindrot.jbcrypt.BCrypt;

import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.auth.api.IAuthPlayer;

class AuthPlayerImpl implements IAuthPlayer
{
    private final INetworkManager networkManager;
    private final Identity        playerIdentity;

    public AuthPlayerImpl(final INetworkManager networkManager, final Identity playerIdentity)
    {
        this.networkManager = networkManager;
        this.playerIdentity = playerIdentity;
    }

    @Override
    public boolean isPremium()
    {
        return this.getUnsafe().map(IPlayer::isPremium).orElse(false);
    }

    @Override
    public boolean isOnline()
    {
        return this.getUnsafe().map(IPlayer::isOnline).orElse(false);
    }

    @Override
    public boolean isRegistered()
    {
        return this.getUnsafe().map(player ->
        {
            final MetaStore store = player.getMetaStore();
            return store.contains(PLAYER_PASSWORD);
        }).orElse(false);
    }

    @Override
    public void unregister()
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(this.playerIdentity))
        {
            final IPlayer player = t.getPlayer();
            player.getMetaStore().remove(PLAYER_PASSWORD);
        }
    }

    @Override
    public void setPassword(final String newPassword)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(this.playerIdentity))
        {
            final String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            final IPlayer player = t.getPlayer();
            player.getMetaStore().set(PLAYER_PASSWORD, newHash);
        }
    }

    @Override
    public boolean checkPassword(final String password)
    {
        return this.getUnsafe().map(player ->
        {
            final MetaStore metaStore = player.getMetaStore();
            if (metaStore.contains(IAuthPlayer.PLAYER_PASSWORD))
            {
                final String hash = metaStore.get(IAuthPlayer.PLAYER_PASSWORD);
                return BCrypt.checkpw(password, hash);
            }

            return false;
        }).orElse(false);
    }

    private Optional<IPlayer> getUnsafe()
    {
        return this.networkManager.getPlayers().unsafe().get(this.playerIdentity);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerIdentity", this.playerIdentity).toString();
    }
}
