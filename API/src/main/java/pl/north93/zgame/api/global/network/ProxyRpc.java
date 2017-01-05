package pl.north93.zgame.api.global.network;

import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;

public interface ProxyRpc
{
    boolean isOnline(String nick); // sprawdza czy gracz jest online na tym proxy

    @DoNotWaitForResponse
    void sendMessage(String nick, String message);

    @DoNotWaitForResponse
    void kick(String nick, String kickMessage);

    @DoNotWaitForResponse
    void connectPlayer(String nick, String serverName);

    @DoNotWaitForResponse
    void connectPlayerToServersGroup(String nick, String serversGroup);

    @DoNotWaitForResponse
    void addServer(ServerProxyData proxyData);

    @DoNotWaitForResponse
    void removeServer(String serverName);

    @DoNotWaitForResponse
    void removeAllServers();
}
