package pl.north93.zgame.api.global.network.proxy;

import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;

public interface IProxyRpc
{
    Boolean isOnline(String nick); // sprawdza czy gracz jest online na tym proxy

    @DoNotWaitForResponse
    void sendMessage(String nick, String message, Boolean colorText);

    @DoNotWaitForResponse
    void kick(String nick, String kickMessage);

    @DoNotWaitForResponse
    void connectPlayer(String nick, String serverName, JoinActionsContainer actions);

    @DoNotWaitForResponse
    void connectPlayerToServersGroup(String nick, String serversGroup, JoinActionsContainer actions);

    @DoNotWaitForResponse
    void addServer(ServerProxyData proxyData);

    @DoNotWaitForResponse
    void removeServer(ServerProxyData proxyData);
}
