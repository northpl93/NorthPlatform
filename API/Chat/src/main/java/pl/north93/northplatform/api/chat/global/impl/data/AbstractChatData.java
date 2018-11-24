package pl.north93.northplatform.api.chat.global.impl.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.redis.event.INetEvent;

public abstract class AbstractChatData implements INetEvent
{
    protected String roomId;
    protected String message; // w jsonie

    public AbstractChatData()
    {
    }

    public AbstractChatData(final String roomId, final String message)
    {
        this.roomId = roomId;
        this.message = message;
    }

    public String getRoomId()
    {
        return this.roomId;
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("roomId", this.roomId).append("message", this.message).toString();
    }
}
