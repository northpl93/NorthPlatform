package pl.north93.zgame.auth.api.player;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public abstract class AuthPlayer
{
    protected static final MetaKey PLAYER_PASSWORD = MetaKey.get("password");

    public abstract boolean isRegistered();

    public abstract void unregister();

    public abstract void setPassword(String newPassword);

    public abstract String getPassword();

    public static AuthPlayer get(final Value<IOnlinePlayer> onlinePlayerValue)
    {
        return new OnlineAuthPlayer(onlinePlayerValue);
    }
}
