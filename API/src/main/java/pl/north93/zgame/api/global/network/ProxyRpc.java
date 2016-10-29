package pl.north93.zgame.api.global.network;

import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;

public interface ProxyRpc
{
    @DoNotWaitForResponse
    void sendMessage(String nick, String message);

    @DoNotWaitForResponse
    void kick(String nick, String kickMessage);
}
