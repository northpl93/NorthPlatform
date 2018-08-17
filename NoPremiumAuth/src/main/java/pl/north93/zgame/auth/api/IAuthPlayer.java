package pl.north93.zgame.auth.api;

import pl.north93.zgame.api.global.metadata.MetaKey;

public interface IAuthPlayer
{
    MetaKey PLAYER_PASSWORD = MetaKey.get("password");
    MetaKey LOGGED_IN       = MetaKey.get("loggedIn");

    boolean isPremium();

    boolean isOnline();

    boolean isRegistered();

    void unregister();

    void setPassword(String newPassword);

    boolean checkPassword(String password);
}
