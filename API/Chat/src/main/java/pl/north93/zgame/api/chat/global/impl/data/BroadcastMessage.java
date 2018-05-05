package pl.north93.zgame.api.chat.global.impl.data;

public class BroadcastMessage extends AbstractChatData
{
    public BroadcastMessage()
    {
    }

    public BroadcastMessage(final String roomId, final String message)
    {
        super(roomId, message);
    }
}
