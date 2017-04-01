package pl.north93.zgame.auth.api.player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

class OnlineAuthPlayer extends AuthPlayer
{
    private final Value<IOnlinePlayer> playerValue;

    public OnlineAuthPlayer(final Value<IOnlinePlayer> playerValue)
    {
        this.playerValue = playerValue;
    }

    @Override
    public boolean isPremium()
    {
        final IOnlinePlayer player = this.playerValue.get();
        return player != null && player.isPremium();
    }

    @Override
    public boolean isRegistered()
    {
        try
        {
            this.playerValue.lock();
            final MetaStore playerMeta = this.playerValue.get().getMetaStore();
            return playerMeta.contains(PLAYER_PASSWORD);
        }
        finally
        {
            this.playerValue.unlock();
        }
    }

    @Override
    public void unregister()
    {
        this.playerValue.update(player ->
        {
            player.getMetaStore().remove(PLAYER_PASSWORD);
        });
    }

    @Override
    public void setPassword(final String newPassword)
    {
        this.playerValue.update(player ->
        {
            player.getMetaStore().setString(PLAYER_PASSWORD, newPassword);
        });
    }

    @Override
    public String getPassword()
    {
        try
        {
            this.playerValue.lock();
            return this.playerValue.get().getMetaStore().getString(PLAYER_PASSWORD);
        }
        finally
        {
            this.playerValue.unlock();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerValue", this.playerValue).toString();
    }
}