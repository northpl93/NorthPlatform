package pl.north93.northplatform.api.global.redis.rpc.impl.messaging;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RpcInvokeMessage
{
    private final String sender;
    private final int classId;
    private final int requestId;
    private final int methodId;
    private final Object[] args;

    // constructor with -parameters for NorthSerializer purposes
    public RpcInvokeMessage(final String sender, final int classId, final int requestId, final int methodId, final Object[] args)
    {
        this.sender = sender;
        this.classId = classId;
        this.requestId = requestId;
        this.methodId = methodId;
        this.args = args;
    }
}
