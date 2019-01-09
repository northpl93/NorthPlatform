package pl.north93.northplatform.webauth;

import java.util.UUID;

public interface IWebAuthManager
{
    String getLoginUrl(UUID playerId);
}
