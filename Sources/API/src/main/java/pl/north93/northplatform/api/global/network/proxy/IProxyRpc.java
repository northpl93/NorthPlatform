package pl.north93.northplatform.api.global.network.proxy;

import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.northplatform.api.global.redis.rpc.annotation.DoNotWaitForResponse;

public interface IProxyRpc
{
    Boolean isOnline(String nick); // sprawdza czy gracz jest online na tym proxy

    @DoNotWaitForResponse
    void sendJsonMessage(String nick, String json);

    @DoNotWaitForResponse
    void kick(String nick, String json);

    @DoNotWaitForResponse
    void connectPlayer(String nick, String serverName, JoinActionsContainer actions);

    @DoNotWaitForResponse
    void addServer(Server proxyData);

    @DoNotWaitForResponse
    void removeServer(Server proxyData);
}
