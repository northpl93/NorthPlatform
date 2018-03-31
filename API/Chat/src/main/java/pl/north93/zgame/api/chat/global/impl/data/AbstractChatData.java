package pl.north93.zgame.api.chat.global.impl.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class AbstractChatData
{
    protected String roomId;
    protected String message;

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
