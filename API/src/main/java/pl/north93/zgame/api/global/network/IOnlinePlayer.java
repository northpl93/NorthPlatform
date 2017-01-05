package pl.north93.zgame.api.global.network;

import java.util.UUID;

import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.redis.observable.ProvidingRedisKey;
import pl.north93.zgame.api.global.utils.Messageable;

public interface IOnlinePlayer extends IPlayer, ProvidingRedisKey, Messageable
{
    void transferDataFrom(IOfflinePlayer offlinePlayer);

    /**
     * Zwraca nick pod którym aktualnie jest zalogowany gracz.
     * @return nick zalogowanego gracza.
     */
    String getNick();

    String getProxyId();

    UUID getServerId();

    void setServerId(UUID serverId);

    boolean isPremium();

    void kick(String message);

    void connectTo(ServerProxyData server);

    void connectTo(String serversGroupName);
}
