package pl.north93.zgame.webauth;

import java.util.UUID;

public interface IWebAuthManager
{
    String getLoginUrl(UUID playerId);
}
