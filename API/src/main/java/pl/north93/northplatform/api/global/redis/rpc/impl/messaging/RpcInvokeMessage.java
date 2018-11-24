package pl.north93.northplatform.api.global.redis.rpc.impl.messaging;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RpcInvokeMessage
{
    private String   sender;
    private Integer  classId;
    private Integer  requestId;
    private Integer  methodId;
    private Object[] args;

    public RpcInvokeMessage()
    {
    }

    public RpcInvokeMessage(final String sender, final Integer classId, final Integer requestId, final Integer methodId, final Object[] args)
    {
        this.sender = sender;
        this.classId = classId;
        this.requestId = requestId;
        this.methodId = methodId;
        this.args = args;
    }

    public String getSender()
    {
        return this.sender;
    }

    public void setSender(final String sender)
    {
        this.sender = sender;
    }

    public Integer getClassId()
    {
        return this.classId;
    }

    public void setClassId(final Integer classId)
    {
        this.classId = classId;
    }

    public Integer getRequestId()
    {
        return this.requestId;
    }

    public void setRequestId(final Integer requestId)
    {
        this.requestId = requestId;
    }

    public Integer getMethodId()
    {
        return this.methodId;
    }

    public void setMethodId(final Integer methodId)
    {
        this.methodId = methodId;
    }

    public Object[] getArgs()
    {
        return this.args;
    }

    public void setArgs(final Object[] args)
    {
        this.args = args;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("sender", this.sender).append("classId", this.classId).append("requestId", this.requestId).append("methodId", this.methodId).append("args", this.args).toString();
    }
}
