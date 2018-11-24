package pl.north93.northplatform.auth.api;

import pl.north93.northplatform.api.global.network.players.Identity;

public interface IAuthManager
{
    IAuthPlayer getPlayer(Identity identity);

    boolean isLoggedIn(String name);

    void setLoggedInStatus(Identity identity, boolean status);

    void deleteStatus(Identity identity);
}
