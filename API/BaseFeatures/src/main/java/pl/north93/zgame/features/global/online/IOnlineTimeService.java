package pl.north93.zgame.features.global.online;

import java.time.Duration;

import pl.north93.zgame.api.global.network.players.Identity;

public interface IOnlineTimeService
{
    Duration getCurrentOnlineTime(Identity identity);

    Duration getTotalOnlineTime(Identity identity);

    void resetTotalOnlineTime(Identity identity);
}
