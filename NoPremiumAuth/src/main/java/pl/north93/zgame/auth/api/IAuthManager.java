package pl.north93.zgame.auth.api;

import java.util.UUID;

public interface IAuthManager
{
    boolean isLoggedIn(UUID uuid);

    void setLoggedInStatus(UUID uuid, boolean status);

    void deleteStatus(UUID uuid);
}
