package pl.north93.northplatform.api.global.redis.rpc.impl.messaging;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RpcResponseMessage
{
    private final int requestId;
    private final Object response;

    public RpcResponseMessage(final int requestId, final @Nullable Object response)
    {
        this.requestId = requestId;
        this.response = response;
    }
}
