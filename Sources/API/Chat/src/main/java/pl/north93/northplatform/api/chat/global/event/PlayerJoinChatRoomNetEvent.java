package pl.north93.northplatform.api.chat.global.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.redis.event.INetEvent;

public class PlayerJoinChatRoomNetEvent implements INetEvent
{
    private Identity identity;
    private String   roomId;

    public PlayerJoinChatRoomNetEvent()
    {
    }

    public PlayerJoinChatRoomNetEvent(final Identity identity, final String roomId)
    {
        this.identity = identity;
        this.roomId = roomId;
    }

    public Identity getIdentity()
    {
        return this.identity;
    }

    public String getRoomId()
    {
        return this.roomId;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("identity", this.identity).append("roomId", this.roomId).toString();
    }
}
